package NetGraphAlgebraDefs

/** Does something very simple */
class ObtainGraphInformation(graph: List[NetGraphComponent]) {
  /** Does something very simple */
  def getNodes: Seq[NodeObject] = { graph.collect{ case node : NodeObject => node} }

  /** Does something very simple */
  def getEdges: Seq[Action] = { graph.collect{ case edge : Action => edge} }
}