package NetGraphAlgebraDefs

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ObtainGraphInformationTest extends AnyFlatSpec with Matchers {
  val NetGraph: List[NetGraphComponent] = List(
    NodeObject(1, 3, 4, 6, 3, 4, 6, 4, 0.98),
    NodeObject(2, 3, 4, 6, 3, 4, 6, 4, 0.98),
    Action(1, NodeObject(1, 3, 4, 6, 3, 4, 6, 4, 0.98), NodeObject(2, 3, 4, 6, 3, 4, 6, 4, 0.98), 1, 2, Some(3), 0.9)
  )

  val result1: Seq[NodeObject] = Seq(
    NodeObject(1, 3, 4, 6, 3, 4, 6, 4, 0.98),
    NodeObject(2, 3, 4, 6, 3, 4, 6, 4, 0.98))

  val result2: Seq[Action] = Seq(
    Action(1, NodeObject(1, 3, 4, 6, 3, 4, 6, 4, 0.98), NodeObject(2, 3, 4, 6, 3, 4, 6, 4, 0.98), 1, 2, Some(3), 0.9))

  val instanceObject = new ObtainGraphInformation(NetGraph)

  "ObtainGraphInformationTest" should "return a list of NodeObjects" in {
    assert(instanceObject.getNodes == result1)
  }

  "ObtainGraphInformationTest" should "return a list of Actions" in {
    assert(instanceObject.getEdges == result2)
  }
}