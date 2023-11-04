package MitMAttacker

import GraphXGeneration.{Edge_, Node}
import Utilz.{CreateLogger, RWAConfig}
import org.apache.spark._
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD

/** A utility object that allows matches to be found between the original and walked ndoes..*/
object Matcher {
  private val logger = CreateLogger(this.getClass)

  /** A class that provides methods to find similarity measures and matches between walked perturbed nodes
  * and the original nodes that have true values in them.*/
  implicit class Match(walkedNodes: RDD[(VertexId, Node)]) extends Serializable {
    /** Returns a list of matched pairs that have a similarity greater than the matched threshold.
     * @param impNodes list of nodes from the original graph that has important information.
     * @return list of node pairs that have similarity greater than the matching threshold.
     * */
    def matchPairs(impNodes: Broadcast[Array[(VertexId, Node)]]):
    List[(((VertexId, Node), (VertexId, Node)), Double)] = {
      logger.info("Matching across each partition now.")
      val matches = walkedNodes.mapPartitions(partition => generateMatches(partition, impNodes.value)).collect
      logger.info("Matching completed.")
      matches.toList
    }

    /** Returns matched pairs for every partition.
     * @param part represents the partitioned walked nodes.
     * @param orgNodes represents the original nodes to be compared with.
     * @return an iterator object containing the list of matched pairs.
     * */
    private def generateMatches(part: Iterator[(VertexId, Node)], orgNodes: Array[(VertexId, Node)]):
    Iterator[(((VertexId, Node), (VertexId, Node)), Double)] = {
      logger.info(s"Matching on partition number: ${part.hashCode()}")
      val partPerNodes = part.toList

      logger.info("Generating similarity measure between every original node and a perturbed node.")
      val partPerNodeSim = partPerNodes.map(perNode => generateSimilarity(perNode, orgNodes))

      logger.info("Filtering pairs based on the Matching Threshold.")
      val thresholdExceedPairs = partPerNodeSim.filter(entry => entry._2 >= RWAConfig.matchingThreshold)

      thresholdExceedPairs.iterator
    }

    /**
     * Generates the original node that has the maximum similarity with the perturbed node.
     *
     * The calculation is done by comparing every parameter of the perturbed node with that of the original node.
     *
     * @param perNode the perturbed node that is to be compared.
     * @param orgNodes the list of original nodes that the perturbed node must be compared with.
     * @return a tuple containing (PerNode, OrgNode, Similarity Measure).
     * */
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

    /**
     * Returns a bit value corresponding to whether two values are equal or not.
     * @param val1 first value to check.
     * @param val2 second value to check.
     * @return a bit value 1 if true and 0 if false.
     * */
    private def validate(val1: Any, val2: Any): Int = {
      if (val1 == val2) { 1 } else { 0 }
    }
  }
}