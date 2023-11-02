package Utilz

import NetGraphAlgebraDefs.NetGraphComponent
import org.slf4j.Logger

import java.io.{FileInputStream, ObjectInputStream}
import java.nio.file.{Files, Paths}

/** Does something very simple */
class FileOperations {
  private val logger: Logger = CreateLogger(this.getClass)

  /** Does something very simple */
  def loadNetGraph(inputDir: String, inputFile: String): Option[List[NetGraphComponent]] = {
    logger.info("Using the current user directory.")
    val currDir: String = System.getProperty("user.dir")

    if (Files.exists(Paths.get(s"$currDir$inputDir$inputFile"))) {
      logger.info(s"Loading the NetGraph from $currDir$inputDir$inputFile")

      val fileStream: FileInputStream = new FileInputStream(s"$currDir$inputDir$inputFile")
      val objectStream: ObjectInputStream = new ObjectInputStream(fileStream)
      val netGraph: List[NetGraphComponent] = objectStream.readObject.asInstanceOf[List[NetGraphComponent]]

      objectStream.close()
      fileStream.close()

      Some(netGraph)
    }
    else {
      logger.error(s"$inputFile not found. Please check the directory / filename again")
      None
    }
  }

  /** Does something very simple */
  def saveGraphXModel(): Unit = {}

  def saveAccuracyModel(): Unit = {}
}