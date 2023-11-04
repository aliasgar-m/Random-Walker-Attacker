package MitMAttacker

import GraphXGeneration.Node
import org.apache.spark.graphx.VertexId
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class AttackerTest extends AnyFlatSpec with Matchers {
  val attackPairs1: List[(((VertexId, Node), (VertexId, Node)), Double)] =
    List((((1L, Node(4, 5, 2, 6, 1, 10, 5, 0.85)),
      (1L, Node(4, 5, 4, 6, 1, 10, 5, 0.85, valuableData = true))), 0.875))

  val attackPairs2: List[(((VertexId, Node), (VertexId, Node)), Double)] =
    List((((3L, Node(3, 2, 7, 6, 5, 10, 5, 0.5, valuableData = true)),
      (3L, Node(3, 4, 7, 5, 5, 10, 5, 0.5, valuableData = true))), 1.0))

  val attackPair3: List[Nothing] = List.empty

  val result1: String = "Failure"
  val result2: String = "Success"
  val result3: String = "Not Found"

  "Attacker Test" should "return a result of Failure when a false positive is found." in {
    assert(Attacker.Attack(attackPairs1).attack() == result1)
  }

  "Attacker Test" should "return a result of Success when a true positive is found." in {
    assert(Attacker.Attack(attackPairs2).attack() == result2)
  }

  "Attacker Test" should "return a result of Not Found when no match is found." in {
    assert(Attacker.Attack(attackPair3).attack() == result3)
  }
}
