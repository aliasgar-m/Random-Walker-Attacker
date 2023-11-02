package GraphXGeneration

import NetGraphAlgebraDefs.{Action, NetGraphComponent, NodeObject, ObtainGraphInformation}
import Utilz.CreateLogger
import org.apache.spark.SparkContext
import org.apache.spark.graphx.{Edge, Graph, VertexId}
import org.apache.spark.rdd.RDD
import org.slf4j.Logger

/** Does something very simple */
class GraphXModel {
  /** Does something very simple */
  private def createNode(node: NodeObject): GraphXGeneration.Node = {
    GraphXGeneration.Node(
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

  /** Does something very simple */
  private def createEdge(edge: Action): Edge_ = {
    Edge_(
      actionType = edge.actionType,
      fromId = edge.fromId,
      toId = edge.toId,
      resultingValue = edge.resultingValue,
      cost = edge.cost)
  }

  /** Does something very simple */
  def createGraph(nodes: RDD[(VertexId, GraphXGeneration.Node)], edges: RDD[Edge[Edge_]]):
  Graph[GraphXGeneration.Node, Edge_] = {
    val graph = Graph(nodes, edges)
    graph
  }
}

/** Does something very simple */
object GraphXModel {
  private val logger : Logger = CreateLogger(classOf[GraphXModel])
  private val instanceObject = new GraphXModel()

  /** Does something very simple */
  def apply(inputNetGraph: List[NetGraphComponent], sc: SparkContext):
  Graph[GraphXGeneration.Node, Edge_] = {
    val graphReader = new ObtainGraphInformation(graph = inputNetGraph)
    val nodesNetGraph: Seq[NodeObject] = graphReader.getNodes
    val edgesNetGraph: Seq[Action] = graphReader.getEdges

    val nodesGraphX: RDD[(VertexId, GraphXGeneration.Node)] = sc.parallelize(nodesNetGraph.map(
      node => (node.id.asInstanceOf[Number].longValue(), instanceObject.createNode(node))))

    val edgesGraphX: RDD[Edge[Edge_]] = sc.parallelize(edgesNetGraph.map(
      edge => Edge(edge.fromNode.id, edge.toNode.id, instanceObject.createEdge(edge))))

    val graph = instanceObject.createGraph(nodesGraphX, edgesGraphX)
    graph.cache()
  }
}