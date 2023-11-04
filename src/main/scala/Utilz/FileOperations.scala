package Utilz

import GraphXGeneration.{Edge_, Node}
import NetGraphAlgebraDefs.{Action, NetGraphComponent, NodeObject}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.dataformat.yaml._
import org.apache.spark.graphx.Graph

import java.io.{File, FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import java.nio.file.{Files, Paths}
import scala.collection.immutable.ListMap

/**
 * A class that provides a set of methods to perform file operations.
 * These operations include:
 *
 * 1. Deserializing and loading a NetGraph.
 *
 * 2. Saving a GraphX model.
 *
 * 3. Saving the accuracy model.
 */
class FileOperations {
  private val logger = CreateLogger(this.getClass)

  /**
   * Returns a List of NetGraph components that represent the input graph. These include [[NodeObject]]
   * and [[Action]].
   *
   * @param inputDir the directory where the input graph's serialized file is stored.
   * @param inputFile the serialized file that needs to be deserialized.
   * @return an option object that contains a list of [[NetGraphComponent]] objects.
   * */
  def loadNetGraph(inputDir: String, inputFile: String): Option[List[NetGraphComponent]] = {
    logger.info("Using the current user directory.")
    val currDir = System.getProperty("user.dir")

    if (Files.exists(Paths.get(s"$currDir$inputDir$inputFile"))) {
      logger.info(s"Found the NetGraph. Loading from $currDir$inputDir$inputFile.")
      val fileStream = new FileInputStream(s"$currDir$inputDir$inputFile")
      val objectStream = new ObjectInputStream(fileStream)

      val netGraph = objectStream.readObject.asInstanceOf[List[NetGraphComponent]]
      objectStream.close()
      fileStream.close()

      Some(netGraph)
    }
    else {
      logger.error(s"$inputFile not found. Please check the user directory / file directory / filename again.")
      None
    }
  }

  /** Saves the GraphX Model to the FileSystem.
   * @param graph the graphX model that needs to be saved. */
  def saveGraphXModel(graph: Graph[Node, Edge_], inputFile: String): Unit = {
    val currDir = System.getProperty("user.dir")
    val fileStream = new FileOutputStream(s"$currDir${RWAConfig.outputDir}$inputFile")
    val objectStream = new ObjectOutputStream(fileStream)

    objectStream.writeObject(graph)
    objectStream.close()
    fileStream.close()
  }

  /** Saves the accuracy model to the FileSystem.
   * @param outcome the generated accuracy model.
   * */
  def saveAccuracyModel(outcome: ListMap[String, Any]): Unit = {
    val currDir = System.getProperty("user.dir")

    val resultFile = new File(s"$currDir${RWAConfig.outputDir}${RWAConfig.resultFile}")
    val mapper = new YAMLMapper()

    mapper.registerModule(DefaultScalaModule)
    mapper.writeValue(resultFile, outcome)
  }
}