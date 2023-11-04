package GraphXGeneration

/**
 * Trait that represents a GraphComponent and acts as an interface to created Nodes and Edges.
 * */
trait GraphComponent extends Serializable

/**
 * Case class that represents a Node attribute.
 * */
@SerialVersionUID(123L)
case class Node(children: Int = 0, props: Int = 0, currentDepth: Int = 1,
                propValueRange:Int = 0, maxDepth:Int = 0, maxBranchingFactor:Int = 0,
                maxProperties:Int = 0, storedValue: Double = 0.0, valuableData: Boolean = false) extends GraphComponent

/**
 * Case class that represents an Edge attribute.
 * */
@SerialVersionUID(123L)
case class Edge_(actionType: Int, fromId: Int, toId: Int,
                resultingValue: Option[Int], cost: Double) extends GraphComponent