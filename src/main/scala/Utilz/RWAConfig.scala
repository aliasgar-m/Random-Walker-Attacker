package Utilz

import com.typesafe.config.{Config, ConfigFactory}

/** Does something very simple */
object RWAConfig {
  private val config: Config = ConfigFactory.load()

  val inputDir: String = config.getString("RWAApp.inputDir")
  val orgInputFile: String = config.getString("RWAApp.orgInputFile")
  val perInputFile: String = config.getString("RWAApp.perInputFile")
  val noOfTrials: Int = config.getInt("RWAApp.noOfTrials")

  val sparkAppName: String = config.getString("RWAApp.spark.appName")
  val sparkMaster: String = config.getString("RWAApp.spark.master")

  val noOfSteps: Int = config.getInt("RWAApp.walker.noOfSteps")
  val noOfWalksPerPartition: Int = config.getInt("RWAApp.walker.noOfWalksPerPartition")
}
