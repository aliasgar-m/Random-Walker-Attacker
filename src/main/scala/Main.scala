package com.lsc

import Utilz.{CreateLogger, FileOperations, RWAConfig}
import org.apache.spark
import org.slf4j.Logger

/** Does something very simple */
object Main {
  private val logger: Logger = CreateLogger(classOf[Main.type])

  /** Does something very simple */
  def main(args: Array[String]): Unit = {
    val sparkSession = spark.sql.SparkSession
      .builder()
      .appName(name = RWAConfig.sparkAppName)
      .master(master = RWAConfig.sparkMaster)
      .getOrCreate()

    val sparkContext = sparkSession.sparkContext

    val FileOperator = new FileOperations()

    val orgNetGraph = FileOperator.loadNetGraph(inputDir = RWAConfig.inputDir, inputFile = RWAConfig.orgInputFile)
    val perNetGraph = FileOperator.loadNetGraph(inputDir = RWAConfig.inputDir, inputFile = RWAConfig.perInputFile)

    if (orgNetGraph.nonEmpty && perNetGraph.nonEmpty) {

    }
    else {
      logger.error("Either the Original or Perturbed NetGraph is empty.")
      logger.error("Since either of the graphs are empty, the process cannot be continued further.")
      logger.error("Exiting.")
    }

    sparkContext.stop()
  }
}