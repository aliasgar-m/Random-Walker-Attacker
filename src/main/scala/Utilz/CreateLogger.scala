package Utilz

import org.slf4j.{Logger, LoggerFactory}

/** Does something very simple */
object CreateLogger {
  /** Does something very simple */
  def apply[T](classLogger: Class[T]): Logger = {
    val logger: Logger = LoggerFactory.getLogger(classLogger.getClass)
    logger
  }
}