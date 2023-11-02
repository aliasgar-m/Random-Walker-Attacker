package Utilz

object Accuracy {
  def generateAccuracy(results: List[(Int, String)]): Map[String, Any] = {
    val overallSuccess = results.filter(entry => entry._2 == "Success")
    val overallFailures = results.filter(entry => entry._2 == "Failure")
    val fileOperator = new FileOperations()

    val resultMap = Map(
      "Number of Trials" -> RWAConfig.noOfTrials,
      "Number of Walks per Partition" -> RWAConfig.noOfWalksPerPartition,
      "Matching Pairs Threshold" -> RWAConfig.matchingThreshold,
      "Attacking Pairs Threshold" -> RWAConfig.attackingThreshold,
      "Total Successes" -> overallSuccess.size,
      "Total Failures" -> overallFailures.size
    )

    fileOperator.saveAccuracyModel()

    resultMap
  }

}
