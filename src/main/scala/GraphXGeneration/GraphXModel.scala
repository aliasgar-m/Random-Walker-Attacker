package GraphXGeneration

import NetGraphAlgebraDefs.{Action, NetGraphComponent, NodeObject, ObtainGraphInformation}
import Utilz.CreateLogger
import org.apache.spark.SparkContext
import org.apache.spark.graphx.{Edge, Graph, VertexId}
import org.apache.spark.rdd.RDD

/**
 * A class that provides methods to create a GraphX Model.
 * */
class GraphXModel {
  /**
   * Returns a [[Graph]] model with nodes and edges.
   * @param nodes an RDD representing a list of a tuple consisting of [[VertexId]] and attribute [[Node]].
   * @param edges an RDD representing a list of [[Edge]] objects with attribute [[Edge_]].
   * @return a [[Graph]] model that can be used to perform parallel computations in Spark.
   * */
  def createGraph(nodes: RDD[(VertexId, Node)], edges: RDD[Edge[Edge_]]): Graph[Node, Edge_] = {
    val graph = Graph(nodes, edges)
    graph
  }

  /**
   * Returns a [[Node]] Object that represents a Node attribute of the GraphX Model.
   *
   * @param node input [[NodeObject]] that needs to be converted.
   * @return a [[Node]] Object.
   * */
  private def createNode(node: NodeObject): Node = {
    Node(
      children = node.children,
      props = node.props,
      currentDepth = node.currentDepth,
      propValueRange = node.propValueRange,
      maxDepth = node.maxDepth,
      maxBranchingFactor = node.maxBranchingFactor,
      maxProperties = node.maxProperties,
      storedValue = node.storedValue,
      valuableData = node.valuableData)
  }

  /**
   * Returns an [[Edge_]] Object that represents an Edge attribute of the GraphX Model.
   *
   * @param edge input [[Action]] that needs to be converted.
   * @return an [[Edge_]] Object.
   * */
  private def createEdge(edge: Action): Edge_ = {
    Edge_(
      actionType = edge.actionType,
      fromId = edge.fromId,
      toId = edge.toId,
      resultingValue = edge.resultingValue,
      cost = edge.cost)
  }
}


/**
 * A companion object that provides an interface to generate a GraphX model and
 * providing a source of abstraction.
 * */
object GraphXModel {
  private val logger = CreateLogger(classOf[GraphXModel])
  private val instanceObject = new GraphXModel()

  /**
   * Returns a GraphX model with Node attribute as [[Node]] and Edge attribute as [[Edge_]].
   * @param inputNetGraph the NetGraph that needs to be converted.
   * @param sc the sparkContext that allows creation of RDDs.
   * @return a graphX model with [[Node]] and [[Edge_]].
   * */
  def apply(inputNetGraph: List[NetGraphComponent], sc: SparkContext): Graph[Node, Edge_] = {
    logger.info("Generating a GraphReader to obtain Node and Edge Information.")
    val graphReader = new ObtainGraphInformation(graph = inputNetGraph)
    val nodesNetGraph = graphReader.getNodes
    val edgesNetGraph = graphReader.getEdges
    logger.info("Node and Edge Information obtained.")

    val nodesGraphX = sc.parallelize(nodesNetGraph.map(
      node => (node.id.asInstanceOf[Number].longValue(), instanceObject.createNode(node))))
    logger.info("Created the RDD of Nodes.")

    val edgesGraphX = sc.parallelize(edgesNetGraph.map(
      edge => Edge(edge.fromNode.id, edge.toNode.id, instanceObject.createEdge(edge))))
    logger.info("Created the RDD of Edges.")

    val graph = instanceObject.createGraph(nodesGraphX, edgesGraphX)
    graph.cache()
  }
}