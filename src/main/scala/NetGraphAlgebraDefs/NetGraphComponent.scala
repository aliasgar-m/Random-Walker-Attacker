package NetGraphAlgebraDefs

/**
 * Trait that represents a NetGraphComponent and acts as an interface to deserialize the NetGraph.
 * */
trait NetGraphComponent extends Serializable

/**
 * Case class that represents a Node of the NetGraph.
 * */
@SerialVersionUID(123L)
case class NodeObject(id: Int, children: Int, props: Int, currentDepth: Int = 1,
                      propValueRange:Int, maxDepth:Int, maxBranchingFactor:Int,
                      maxProperties:Int, storedValue: Double, valuableData: Boolean = false) extends NetGraphComponent

/**
 * Case class that represents an Edge of the NetGraph.
 * */
@SerialVersionUID(123L)
case class Action(actionType: Int, fromNode: NodeObject, toNode: NodeObject,
                  fromId: Int, toId: Int, resultingValue: Option[Int], cost: Double) extends NetGraphComponent

/**
 * Case class that represents a Terminal Node of the NetGraph.
 * */
@SerialVersionUID(123L)
case object TerminalNode extends NetGraphComponent

/**
 * Case class that represents a Terminal Edge of the NetGraph.
 * */
@SerialVersionUID(123L)
case object TerminalAction extends NetGraphComponent