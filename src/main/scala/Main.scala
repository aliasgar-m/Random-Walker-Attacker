package com.lsc

import GraphXGeneration.GraphXModel
import Utilz.{CreateLogger, FileOperations, RWAConfig}
import org.apache.spark

/** Does something very simple */
object Main {
  private val logger = CreateLogger(classOf[Main.type])

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
      val orgGraphXModel = GraphXModel(inputNetGraph = orgNetGraph.get, sc = sparkContext)
      val perGraphXModel = GraphXModel(inputNetGraph = perNetGraph.get, sc = sparkContext)
    }
    else {
      logger.error("Either the Original or Perturbed NetGraph is empty.")
      logger.error("Since either of the graphs are empty, the process cannot be continued further.")
      logger.error("Exiting.")
    }

    sparkContext.stop()
  }
}

// CREATE BROADCAST VARIABLE TO STORE ALL RANDOM WALKS.
// ADD CHECK TO ENSURE RANDOM WALK NOT COMPUTED AGAIN.
// CREATE VISUALIZATION IF POSSIBLE.
// NEED TO LOG THE WALKS GENERATED I.E. THE BROADCAST VARIABLE, SEE HOW THAT CAN BE DONE.
// ADD LOGGING STATEMENTS AND LOOK AT CODE IF IT REQUIRES MORE CLEANING
// ALLOW USER TO SELECT THE PARAMETERS FOR SIMILARITY THAT WILL BE COUNTED FOR MATCHING / SIMILARITY MEASURE.
