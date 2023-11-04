package MitMAttacker

import GraphXGeneration.Node
import org.apache.spark
import Matcher.Match
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.graphx.VertexId
import org.apache.spark.rdd.RDD
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MatcherTest extends AnyFlatSpec with Matchers {
  val sparkSession = spark.sql.SparkSession
    .builder()
    .appName(name = "Testing")
    .master(master = "local")
    .getOrCreate()

  val sparkContext = sparkSession.sparkContext

  val orgNodes: Broadcast[Array[(VertexId, Node)]] = sparkContext.broadcast(Array(
    (1L, Node(4, 5, 2, 6, 1, 10, 5, 0.85)),
    (2L, Node()),
    (3L, Node(3, 2, 7, 6, 5, 10, 5, 0.5, valuableData = true))))


  val perNode: RDD[(VertexId, Node)]
  = sparkContext.parallelize(List((3L, Node(3, 2, 7, 6, 5, 10, 5, 0.5, valuableData = true))))

  val result: (((VertexId, Node), (VertexId, Node)), Double) = (
    ((3L, Node(3, 2, 7, 6, 5, 10, 5, 0.5, valuableData = true)),
    (3L, Node(3, 2, 7, 6, 5, 10, 5, 0.5, valuableData = true))),
    1.0)

  "Matcher Class" should "generate a tuple consisting of the perturbed node and " +
    "the original that has the maximum similarity." in {
    assert(perNode.matchPairs(impNodes = orgNodes).head.equals(result))
  }
}