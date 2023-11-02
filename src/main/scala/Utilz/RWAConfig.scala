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

  val childrenWeight: Double = config.getDouble("RWAApp.matcher.childrenWeight")
  val propertiesWeight: Double = config.getDouble("RWAApp.matcher.propertiesWeight")
  val currentDepthWeight: Double = config.getDouble("RWAApp.matcher.currentDepthWeight")
  val propValueRangeWeight: Double = config.getDouble("RWAApp.matcher.propValueRangeWeight")
  val maxDepthWeight: Double = config.getDouble("RWAApp.matcher.maxDepthWeight")
  val maxBranchingFactorWeight: Double = config.getDouble("RWAApp.matcher.maxBranchingFactorWeight")
  val maxPropertiesWeight: Double = config.getDouble("RWAApp.matcher.maxPropertiesWeight")
  val storedValueWeight: Double = config.getDouble("RWAApp.matcher.storedValueWeight")

  val matchingThreshold: Double = config.getDouble("RWAApp.matcher.matchingThreshold")
}
