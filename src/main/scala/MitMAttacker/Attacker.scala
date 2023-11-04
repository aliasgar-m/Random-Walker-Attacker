package MitMAttacker

import GraphXGeneration.Node
import Utilz.{CreateLogger, RWAConfig}
import org.apache.spark.graphx._

import scala.util.Random

/**
 * A utility object that provides an interface to attack a select node from the list of macthed nodes,
 * */
object Attacker {
  private val logger = CreateLogger(this.getClass)

  /** A class that provides functions to attack a particular node when its similarity is greater than the attacking
   * threshold. */
  implicit class Attack(matchedPairs: List[(((VertexId, Node), (VertexId, Node)), Double)]) extends Serializable {
    /**
     * Returns the result of an attack.
     *
     * This function chooses a node at random from the list of nodes that has further been filtered based
     * on an attacking threshold and attacks that particular node.
     *
     * @return the result indicating "Success" or "Failure".
     * */
    def attack(): String = {
      logger.info("Now filtering the matched pairs based on the attacking threshold.")
      val attackPairs = matchedPairs.filter(entry => entry._2 >= RWAConfig.attackingThreshold)
      if (attackPairs.nonEmpty) {
        val randomAttackedPair = Random.shuffle(attackPairs).head

        logger.info("Now attacking the selected pair.")
        if (randomAttackedPair._1._1._2.valuableData == randomAttackedPair._1._2._2.valuableData) {
          if (randomAttackedPair._1._1._1 == randomAttackedPair._1._2._1) { "Success" } else { "Failure" }
        } else { "Failure" }
      }
      else {
        logger.info("No valid matching pairs found.")
        "Not Found"
      }
    }
  }
}
