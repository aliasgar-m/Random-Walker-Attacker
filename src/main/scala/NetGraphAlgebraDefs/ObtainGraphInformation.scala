package NetGraphAlgebraDefs

/**
 * A class that provides methods to retrieve Nodes and Edges from the List of [[NetGraphComponent]].
 * */
class ObtainGraphInformation(graph: List[NetGraphComponent]) {
  /**
   * Returns a sequence of [[NodeObject]] that represents the Nodes in a NetGraph.
   * */
  def getNodes: Seq[NodeObject] = { graph.collect{ case node : NodeObject => node} }

  /**
   * Returns a sequence of [[Action]] that represent the Edges in a NetGraph.
   * */
  def getEdges: Seq[Action] = { graph.collect{ case edge : Action => edge} }
}