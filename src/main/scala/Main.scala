package com.lsc

import GraphXGeneration.GraphXModel
import MitMAttacker.RandomWalker.Walker
import Utilz.{CreateLogger, FileOperations, RWAConfig}
import org.apache.spark
import org.apache.spark.graphx

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

      val trails = List.range(0, RWAConfig.noOfTrials)
      val trailResults = trails.map(trial => trial -> perGraphXModel.walk()
      )
    }
    else {
      logger.error("Either the Original or Perturbed NetGraph is empty.")
      logger.error("Since either of the graphs are empty, the process cannot be continued further.")
      logger.error("Exiting.")
    }

    sparkContext.stop()
  }
}

// VERY IMPORTANT
// NEED TO ADD DEFAULT VALUE TO MAP TO TAKE INTO CONSIDERATION DISJOINT MAP
// SEND SPARK CONTEXT AS MESSAGE TO ALL NODES
// ASK PROFESSOR HOW TO USE CLASSES TAGS WITH USER DEFINED CLASSES.
// CAN WE WALK BY CREATING SUBGRAPH? TO OPTIMIZE CODE BY REDUCING DEPENDENT PARAMS OF A FUNCTION?
// SEND ORG GRAPH AS A BROADCAST AS WELL.


// CREATE BROADCAST VARIABLE TO STORE ALL RANDOM WALKS.
// ADD CHECK TO ENSURE RANDOM WALK NOT COMPUTED AGAIN.
// CREATE VISUALIZATION IF POSSIBLE.
// NEED TO LOG THE WALKS GENERATED I.E. THE BROADCAST VARIABLE, SEE HOW THAT CAN BE DONE.
// ADD LOGGING STATEMENTS AND LOOK AT CODE IF IT REQUIRES MORE CLEANING
// ALLOW USER TO SELECT THE PARAMETERS FOR SIMILARITY THAT WILL BE COUNTED FOR MATCHING / SIMILARITY MEASURE.
