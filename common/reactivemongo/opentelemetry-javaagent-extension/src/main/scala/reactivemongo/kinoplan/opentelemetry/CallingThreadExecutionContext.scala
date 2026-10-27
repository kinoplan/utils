package reactivemongo.kinoplan.opentelemetry

import java.util.logging.{Level, Logger}

import scala.concurrent.ExecutionContext

// Ending a span is cheap, so it runs on the thread that completes the response future
object CallingThreadExecutionContext extends ExecutionContext {
  private val logger = Logger.getLogger(getClass.getName.stripSuffix("$"))

  override def execute(runnable: Runnable): Unit = runnable.run()

  override def reportFailure(cause: Throwable): Unit =
    logger.log(Level.FINE, "Failed to end a ReactiveMongo span", cause)

}
