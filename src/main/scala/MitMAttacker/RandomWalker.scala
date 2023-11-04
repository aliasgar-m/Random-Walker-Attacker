package MitMAttacker

import GraphXGeneration.{Edge_, Node}
import Utilz.{CreateLogger, RWAConfig}
import org.apache.spark.graphx
import org.apache.spark.graphx.{EdgeTriplet, Graph, VertexId}
import org.apache.spark.rdd.RDD
import org.apache.spark.util.CollectionAccumulator

import scala.annotation.tailrec
import scala.util.Random

/**
 * A utility object that allows random walks to be performed in parallel across various computing nodes.
 * */
object RandomWalker {
  private val logger = CreateLogger(RandomWalker.getClass)

  /**
   * A class that provides methods to perform random walks.
   * */
  implicit class Walker(graph: Graph[Node, Edge_]) extends Serializable {
    /**
     * Returns an RDD containing a list of walked nodes across all partitions which can be used to perform matching
     * in parallel.
     * @param walkAcc the accumulator variable used to store all the unique walks across the computing nodes.
     * @return an RDD containing a list of walked nodes.
     * */
    def walk(walkAcc: CollectionAccumulator[List[(VertexId, Node)]]): RDD[(VertexId, Node)] = {
      logger.info("Walking across each partition now.")
      val partitionWalks = graph.triplets.mapPartitions(partition => generateWalkedNodes(partition, walkAcc))
      logger.info("Walking completed.")
      partitionWalks
    }

    /**
     * Returns a list of distinct walked nodes across an iteration of walks.
     *
     * This function also tries to remove redundancy between walks performed by adding unique walks to
     * an accumulator variable and substituting an already performed walk with an empty substitute.
     *
     * @param partition the partition containing the edge triplets which will be used to generate the walk.
     * @param walkAcc the accumulator variable used to store all the unique walks across the computing nodes.
     * @return a list of distinct nodes with a node represented as (Vertex ID - [[VertexId]], Node Attribute - [[Node]])
     * */
    private def generateWalkedNodes(partition: Iterator[EdgeTriplet[Node, Edge_]],
                                    walkAcc: CollectionAccumulator[List[(VertexId, Node)]]):
    Iterator[(VertexId, Node)] = {
      logger.info(s"Walking on partition number: ${partition.hashCode()}.")
      val partitionList = partition.toList
      logger.info("Generating unique nodes from the partition.")
      val partitionNodes = getDistinctNodes(partitionList)

      logger.info("Generating unique edges from the partition.")
      val partitionEdges = getDistinctEdges(partitionList)

      val iteratedWalks = List.range(0, RWAConfig.noOfWalksPerPartition)

      logger.info(s"Generating ${iteratedWalks.length} walks.")
      val walks = iteratedWalks.flatMap(walk => {
        logger.info(s"Walk no $walk started.")
        val newWalk = startWalk(partitionNodes, partitionEdges)

        if (walkAcc.value.contains(newWalk)) {
          logger.info("Generated walk already exists. Skipping.")
          val emptySub = List((0L, Node()))
          emptySub
        }
        else {
          logger.info("Generated walk added.")
          walkAcc.add(newWalk)
          newWalk
        }
      }).distinct.filter(entry => !entry.equals((0L, Node())))
      walks.iterator
    }

    /**
     * Returns a list of distinct nodes from a partition that can be used to create random walks.
     * @param partitionList the list of triplets generated from a partition.
     * @return a list of node tuples comprising (Vertex ID - [[VertexId]], Node Attribute - [[Node]]).
     * */
    private def getDistinctNodes(partitionList: List[EdgeTriplet[Node, Edge_]]): List[(VertexId, Node)] = {
      partitionList.map(triplet => (triplet.toTuple._1, triplet.toTuple._2))
        .flatMap { case (a, b) => List(a, b) }
        .distinct
    }

    /**
     * Returns a list of distinct edges from a partition that can be used to determine the neighbours of a node.
     * @param partitionList the list of triplets generated from a partition.
     * @return a list of tuples comprising (Source Node - [[VertexId]], Destination Node - [[VertexId]],
     *         Edge Attribute - [[Edge_]])
     * */
    private def getDistinctEdges(partitionList: List[EdgeTriplet[Node, Edge_]]): List[(VertexId, VertexId, Edge_)] = {
      partitionList.map(triplet => (triplet.srcId, triplet.dstId, triplet.attr))
        .distinct
    }

    /**
     * Returns a list of randomly walked nodes.
     *
     * @param nodes list of nodes which will be used for random walking.
     * @param edges list of edges which will be used for determining neighbours.
     * @return a list of randomly walked nodes of type Tuple(Vertex ID - [[VertexId]], Node Attribute - [[Node]])
     */
    @tailrec private def startWalk(nodes: List[(VertexId, Node)], edges: List[(VertexId, VertexId, Edge_)],
                                   walkedNodes: List[(VertexId, Node)] = List.empty): List[(VertexId, Node)] = {
      if (walkedNodes.length == RWAConfig.noOfSteps || checkTerminalNode(walkedNodes, edges)) {
        { walkedNodes }
      } else {
        val currentNode = getCurrentNode(walkedNodes, nodes, edges)
        startWalk(nodes, edges, currentNode :: walkedNodes)
      }
    }

    /**
     * Returns a boolean value stating whether a particular node is a terminal node or not.
     *
     * False - Node not a terminal node
     *
     * True - Node is a terminal node.
     *
     * @param nodes the list of nodes containing the walked nodes whose first node is to be checked for end condition
     *              of terminal node.
     * @param edges the list of edges that can be used to determine the neighbors of a node.
     * @return returns a boolean value indicating terminal node or not.
     */
    private def checkTerminalNode(nodes: List[(VertexId , Node)],
                                  edges: List[(graphx.VertexId, graphx.VertexId, Edge_)]): Boolean = {
      if (nodes.isEmpty) {false}
      else {
        if (edges.filter(edge => edge._1 == nodes.head._1).map(edge => edge._2).isEmpty) { true } else { false }
      }
    }

    /**
     *  Returns a node at random that represents the current node of the walk.
     *
     *  If the walkedNodes parameter is empty indicating no nodes have been walked then the function chooses a node
     *  randomly from the list of input nodes.
     *
     *  If the walkedNodes parameter is not empty indicating a walk has started then chooses a neighbouring node
     *  from a list of neighbours whose edges have the source node as the first node of the walkedNodes list.
     *
     * @param walkedNodes list containing the nodes currently walked.
     * @param nodes list of all nodes contained in the partition.
     * @param edges list of all edges contained in the partition.
     * @return a random node comprising (Vertex ID - [[VertexId]], Node Attribute - [[Node]]).
     */
    private def getCurrentNode(walkedNodes: List[(graphx.VertexId, Node)],
                               nodes: List[(graphx.VertexId, Node)],
                               edges: List[(graphx.VertexId, graphx.VertexId, Edge_)]): (VertexId, Node) = {
      if (walkedNodes.isEmpty) {
        val randomNode = nodes(Random.nextInt(nodes.length))
        randomNode
      }
      else {
        val currentNode = walkedNodes.head
        val neighbors = edges.filter(edge => edge._1 == currentNode._1).map(edge => edge._2)
        val randomNode = Random.shuffle(neighbors).head

        nodes.filter(node => node._1 == randomNode).head
      }
    }
  }
}