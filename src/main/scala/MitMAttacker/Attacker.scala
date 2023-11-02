package MitMAttacker

import GraphXGeneration.Node
import Utilz.{CreateLogger, RWAConfig}
import org.apache.spark.graphx._
import org.slf4j.Logger

import scala.util.Random

object Attacker {
  private val logger: Logger = CreateLogger(this.getClass)

  implicit class Attack(matchedPairs: List[(((VertexId, Node), (VertexId, Node)), Double)]) extends Serializable {
    def attack(): String = {
      val attackPairs = matchedPairs.filter(entry => entry._2 >= RWAConfig.attackingThreshold)
      val randomAttackedPair = Random.shuffle(attackPairs).head

      val result = generateResult(randomAttackedPair)
      result
    }

    private def generateResult(attackPair: (((VertexId, Node), (VertexId, Node)), Double)): String = {
      if (attackPair._1._1._2.valuableData == attackPair._1._2._2.valuableData) {
        if (attackPair._1._1._1 == attackPair._1._2._1) {"Success"} else {"Failure"}
      } else {"Failure"}
    }
  }
}
