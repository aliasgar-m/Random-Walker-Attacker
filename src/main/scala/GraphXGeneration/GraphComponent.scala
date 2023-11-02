package GraphXGeneration

/** Does something very simple */
trait GraphComponent extends Serializable

/** Does something very simple */
@SerialVersionUID(123L)
case class Node(children: Int, props: Int, currentDepth: Int = 1,
                propValueRange:Int, maxDepth:Int, maxBranchingFactor:Int,
                maxProperties:Int, storedValue: Double, valuableData: Boolean = false) extends GraphComponent

/** Does something very simple */
@SerialVersionUID(123L)
case class Edge(actionType: Int, fromId: Int, toId: Int,
                resultingValue: Option[Int], cost: Double) extends GraphComponent