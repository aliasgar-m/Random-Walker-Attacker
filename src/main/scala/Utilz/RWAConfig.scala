package Utilz

import com.typesafe.config.{Config, ConfigFactory}

/**
 * A utility object that provides access to the application's configuration parameters.
 */
object RWAConfig {
  private val config: Config = ConfigFactory.load()

  val inputDir: String = config.getString("RWAApp.inputDir")
  val orgInputFile: String = config.getString("RWAApp.orgInputFile")
  val perInputFile: String = config.getString("RWAApp.perInputFile")
  val logbackXmlFile: String = config.getString("RWAApp.logback-xml")
  val outputDir: String = config.getString("RWAApp.outputDir")
  val orgGraphXFile: String = config.getString("RWAApp.orgGraphXFile")
  val perGraphXFile: String = config.getString("RWAApp.perGraphXFile")
  val resultFile: String = config.getString("RWAApp.resultFile")

  val sparkAppName: String = config.getString("RWAApp.spark.appName")
  val sparkMaster: String = config.getString("RWAApp.spark.master")

  val noOfTrials: Int = config.getInt("RWAApp.noOfTrials")
  val noOfWalksPerPartition: Int = config.getInt("RWAApp.walker.noOfWalksPerPartition")
  val noOfSteps: Int = config.getInt("RWAApp.walker.noOfSteps")

  val childrenWeight: Double = config.getDouble("RWAApp.matcher.childrenWeight")
  val propertiesWeight: Double = config.getDouble("RWAApp.matcher.propertiesWeight")
  val currentDepthWeight: Double = config.getDouble("RWAApp.matcher.currentDepthWeight")
  val propValueRangeWeight: Double = config.getDouble("RWAApp.matcher.propValueRangeWeight")
  val maxDepthWeight: Double = config.getDouble("RWAApp.matcher.maxDepthWeight")
  val maxBranchingFactorWeight: Double = config.getDouble("RWAApp.matcher.maxBranchingFactorWeight")
  val maxPropertiesWeight: Double = config.getDouble("RWAApp.matcher.maxPropertiesWeight")
  val storedValueWeight: Double = config.getDouble("RWAApp.matcher.storedValueWeight")

  val matchingThreshold: Double = config.getDouble("RWAApp.matcher.matchingThreshold")
  val attackingThreshold: Double = config.getDouble("RWAApp.attacker.attackingThreshold")
}