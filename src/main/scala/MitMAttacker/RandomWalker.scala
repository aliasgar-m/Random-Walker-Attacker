package MitMAttacker

import GraphXGeneration.{Edge_, Node}
import Utilz.{CreateLogger, RWAConfig}
import org.apache.spark.graphx
import org.apache.spark.graphx.{EdgeTriplet, Graph, VertexId}
import org.slf4j.Logger

import scala.annotation.tailrec
import scala.util.Random

/** Does something very simple */
object RandomWalker {
  private val logger: Logger = CreateLogger(RandomWalker.getClass)

  implicit class Walker(graph: Graph[Node, Edge_]) extends Serializable {
    private def checkTerminalNode(nodes: List[(VertexId , Node)],
                                  edges: List[(graphx.VertexId, graphx.VertexId, Edge_)]): Boolean = {
      if (nodes.isEmpty) {false}
      else {
        if (edges.filter(edge => edge._1 == nodes.head._1).map(edge => edge._2).isEmpty) { true } else { false }
      }
    }

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

    @tailrec private def startWalk(nodes: List[(graphx.VertexId, Node)],
                                   edges: List[(graphx.VertexId, graphx.VertexId, Edge_)],
                                   walkedNodes: List[(VertexId, Node)] = List.empty):
    List[(VertexId, Node)] = {
      if (walkedNodes.length == RWAConfig.noOfSteps || checkTerminalNode(walkedNodes, edges)) {
        { walkedNodes }
      } else {
        val currentNode = getCurrentNode(walkedNodes, nodes, edges)
        startWalk(nodes, edges, currentNode :: walkedNodes)
      }
    }


    private def getDistinctNodes(part: List[EdgeTriplet[Node, Edge_]]):
    List[(graphx.VertexId, Node)] = {
      part.map(triplet => (triplet.toTuple._1, triplet.toTuple._2))
        .flatMap { case (a, b) => List(a, b) }
        .distinct
    }

    private def getDistinctEdges(part: List[EdgeTriplet[Node, Edge_]]):
    List[(graphx.VertexId, graphx.VertexId, Edge_)] = {
      part.map(triplet => (triplet.srcId, triplet.dstId, triplet.attr)).distinct
    }

    private def generateWalkedNodes(partition: Iterator[EdgeTriplet[Node, Edge_]]) = {
      val partitionList = partition.toList
      val partitionNodes: List[(graphx.VertexId, Node)] = getDistinctNodes(partitionList)
      val partitionEdges: List[(graphx.VertexId, graphx.VertexId, Edge_)] = getDistinctEdges(partitionList)

      val iteratedWalks = List.range(0, RWAConfig.noOfWalksPerPartition)
      val walks = iteratedWalks.flatMap(_ => startWalk(partitionNodes, partitionEdges))
      walks.iterator
    }

    final def walk() = {
      val partitionWalks = graph.triplets.mapPartitions(partition => generateWalkedNodes(partition))
      partitionWalks
    }
  }
}
