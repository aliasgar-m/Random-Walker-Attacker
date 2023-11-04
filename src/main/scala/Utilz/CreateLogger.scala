package Utilz

import org.slf4j.{Logger, LoggerFactory}
import scala.util.{Failure, Success, Try}

/**
 * A utility object that creates a logger to log INFO, WARN and ERROR Statements.
 */
object CreateLogger {
  /**
   *  Method for creating a logger for a particular class.
   * @param classLogger the class for which the logger is to be created.
   * @return an object which is an instance of [[Logger]].
   */
  def apply[T](classLogger: Class[T]): Logger = {
    val logbackXml = RWAConfig.logbackXmlFile
    val logger = LoggerFactory.getLogger(classLogger.getClass)

    Try(getClass.getClassLoader.getResourceAsStream(logbackXml)) match {
      case Failure(exception) => logger.error(s"Failed to locate $logbackXml for reason $exception")
      case Success(inStream) => if (inStream != null) { inStream.close() }
    }
    logger
  }
}