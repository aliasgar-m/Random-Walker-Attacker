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

    sparkContext.stop()
  }
}