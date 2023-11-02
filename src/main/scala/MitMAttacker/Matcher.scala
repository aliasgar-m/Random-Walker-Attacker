package MitMAttacker

import GraphXGeneration.{Edge_, Node}
import Utilz.{CreateLogger, RWAConfig}

import org.apache.spark.graphx
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.slf4j.Logger

/** Does something very simple */
object Matcher {
  private val logger: Logger = CreateLogger(this.getClass)

  /** Does something very simple */
  implicit class Match(walkedNodes: RDD[(VertexId, Node)]) extends Serializable {
    /** Does something very simple */
    def matchPairs(orgGraph: Graph[Node, Edge_]): List[(((VertexId, Node), (VertexId, Node)), Double)] = {
      val impOriginalNodes = orgGraph.vertices.filter(node => node._2.valuableData).collect
      val matches = walkedNodes.mapPartitions(partition => generateMatches(partition, impOriginalNodes)).collect

      println(s"Matched: ${matches.toList}")
      matches.toList
    }

    /** Does something very simple */
    private def generateMatches(part: Iterator[(VertexId, Node)], orgNodes: Array[(VertexId, Node)]):
    Iterator[(((VertexId, Node), (VertexId, Node)), Double)] = {
      val partPerNodes = part.toList
      val partPerNodeSim = partPerNodes.map(perNode => generateSimilarity(perNode, orgNodes))
      val thresholdExceedPairs = partPerNodeSim.filter(entry => entry._2 >= RWAConfig.matchingThreshold)

      thresholdExceedPairs.iterator
    }

    /** Does something very simple */
    private def generateSimilarity(perNode: (graphx.VertexId, Node), orgNodes: Array[(graphx.VertexId, Node)]):
    (((VertexId, Node), (VertexId, Node)), Double) = {
      val similarityMeasureMap = orgNodes.map(orgNode => (perNode, orgNode) -> {
        RWAConfig.childrenWeight * validate(perNode._2.children, orgNode._2.children) +
        RWAConfig.propertiesWeight * validate(perNode._2.props, orgNode._2.props) +
        RWAConfig.currentDepthWeight * validate(perNode._2.currentDepth, orgNode._2.currentDepth) +
        RWAConfig.propValueRangeWeight * validate(perNode._2.propValueRange, orgNode._2.propValueRange) +
        RWAConfig.maxDepthWeight * validate(perNode._2.maxDepth, orgNode._2.maxDepth) +
        RWAConfig.maxBranchingFactorWeight * validate(perNode._2.maxBranchingFactor, orgNode._2.maxBranchingFactor) +
        RWAConfig.maxPropertiesWeight * validate(perNode._2.maxProperties, orgNode._2.maxProperties) +
        RWAConfig.storedValueWeight * validate(perNode._2.storedValue, orgNode._2.storedValue)
      }).toMap

      similarityMeasureMap.filter(entry => entry._2 == similarityMeasureMap.values.max).head
    }

    /** Does something very simple */
    private def validate(val1: Any, val2: Any): Int = {
      if (val1 == val2) { 1 } else { 0 }
    }
  }
}