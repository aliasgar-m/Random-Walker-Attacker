package com.lsc

import GraphXGeneration.{GraphXModel, Node}
import MitMAttacker.Attacker.Attack
import MitMAttacker.Matcher.Match
import MitMAttacker.RandomWalker.Walker
import Utilz.Accuracy.generateAccuracy
import Utilz.{CreateLogger, FileOperations, RWAConfig}
import org.apache.spark
import org.apache.spark.graphx.VertexId

import java.io.File

/**
 * A utility object that provides an entry point for the project.
 */
object Main {
  private val logger = CreateLogger(this.getClass)

  /**
   *  Replicates an Insider Attacker trying to gain access to company's secure computers
   *  in order to steal valuable data and returns the result of the attack.
   *
   *  This program is performed in a distributed environment and can act as a toolkit to
   *  test a company's defense mechanism.
   *
   */
  def main(args: Array[String]): Unit = {
    logger.info("Starting Spark Session.")
    val sparkSession = spark.sql.SparkSession
      .builder()
      .appName(name = RWAConfig.sparkAppName)
      .master(master = RWAConfig.sparkMaster)
      .getOrCreate()

    val sparkContext = sparkSession.sparkContext
    val FileOperator = new FileOperations()

    logger.info("Deserializing and Loading the Original NetGraph.")
    val orgNetGraph = FileOperator.loadNetGraph(inputDir = RWAConfig.inputDir, inputFile = RWAConfig.orgInputFile)

    logger.info("Deserializing and Loading the Perturbed NetGraph.")
    val perNetGraph = FileOperator.loadNetGraph(inputDir = RWAConfig.inputDir, inputFile = RWAConfig.perInputFile)

    if (orgNetGraph.nonEmpty && perNetGraph.nonEmpty) {
      logger.info("Original and Perturbed Graphs have been deserialized and loaded.")
      logger.info("Now generating GraphX Models for the Original and Perturbed Graphs.")

      val orgGraphXModel = GraphXModel(inputNetGraph = orgNetGraph.get, sc = sparkContext)
      logger.info("Original GraphX Model generated.")

      val impOrgGraphNodes = orgGraphXModel.vertices.filter(node => node._2.valuableData).collect
      logger.info("Important nodes of the original graph have been retrieved.")

      val perGraphXModel = GraphXModel(inputNetGraph = perNetGraph.get, sc = sparkContext)
      logger.info("Perturbed GraphX Model generated.")

      val trails = List.range(0, RWAConfig.noOfTrials)
      val broadcastOrgGraphImpNodes = sparkContext.broadcast(impOrgGraphNodes)

      logger.info(s"Performing walks, matches, and attacks on the perturbed graph for ${trails.length} trials" +
        s"and generating a results map.")
      val trailResults = trails.map(trial => trial -> {
        val walkAccumulator = sparkContext.collectionAccumulator[List[(VertexId, Node)]]
        perGraphXModel.walk(walkAccumulator).matchPairs(broadcastOrgGraphImpNodes).attack()})

      logger.info("Generating the Accuracy Model.")
      val accuracyModel = generateAccuracy(results = trailResults)

      val currDir = System.getProperty("user.dir")
      val resultantDir = new File(s"$currDir${RWAConfig.outputDir}")

      if (resultantDir.exists()) {
        resultantDir.delete()
        resultantDir.mkdir()
      } else { resultantDir.mkdir() }

      logger.info("Saving the Original GraphX Model.")
      FileOperator.saveGraphXModel(graph = orgGraphXModel, inputFile = RWAConfig.orgGraphXFile)

      logger.info("Saving the Perturbed GraphX Model.")
      FileOperator.saveGraphXModel(graph = perGraphXModel, inputFile = RWAConfig.perGraphXFile)

      logger.info("Saving the accuracy model.")
      FileOperator.saveAccuracyModel(outcome = accuracyModel)

    }
    else {
      logger.error("Either the Original or Perturbed NetGraph is empty.")
      logger.error("Since either of the graphs are empty, the process cannot be continued further.")
      logger.error("Exiting.")
    }
    sparkContext.stop()
  }
}