package Utilz

import scala.collection.immutable.ListMap

/** A utility object that generates a Accuracy based model.*/
object Accuracy {
  /**
   * Returns a map consisting of various parameters that can be used to determine the accuracy of the model.
   * @param results a list of strings denoting the result of a single iteration of the RWA algorithm.
   * @return an accuracy map.
   * */
  def generateAccuracy(results: List[(Int, String)]): ListMap[String, Any] = {
    val overallSuccess = results.filter(entry => entry._2 == "Success")
    val overallFailures = results.filter(entry => entry._2 == "Failure")
    val overallDeadEnds = results.filter(entry => entry._2 == "Not Found")

    ListMap(
      "Number of Trials" -> RWAConfig.noOfTrials,
      "Number of Walks per Partition" -> RWAConfig.noOfWalksPerPartition,
      "Steps per walk" -> RWAConfig.noOfSteps,
      "Matching Pairs Threshold" -> RWAConfig.matchingThreshold,
      "Attacking Pairs Threshold" -> RWAConfig.attackingThreshold,
      "Total Successful Trials" -> overallSuccess.size,
      "Total Failed Trials" -> overallFailures.size,
      "Total DeadEnds Trials" -> overallDeadEnds.size,
      "Success Probability Percentage" -> (overallSuccess.size * 100 / RWAConfig.noOfTrials)
    )
  }
}