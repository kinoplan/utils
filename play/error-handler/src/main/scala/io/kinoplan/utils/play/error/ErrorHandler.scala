package io.kinoplan.utils.play.error

import javax.inject.{Inject, Singleton}

import scala.concurrent.Future
import scala.util.Try

import play.api.{Configuration, UsefulException}
import play.api.http.{HttpErrorHandler, HttpErrorHandlerExceptions}
import play.api.mvc._
import play.api.mvc.Results._

import io.kinoplan.utils.play.request.RequestMapContext
import io.kinoplan.utils.scala.logging.Loggable
import io.kinoplan.utils.scala.logging.context.MapContext

@Singleton
class ErrorHandler @Inject() (configuration: Configuration) extends HttpErrorHandler with Loggable {

  private val mode: Option[String] = configuration
    .getOptional[String]("application.mode")
    .map(_.toLowerCase)

  private val isProd: Boolean = mode.contains("prod")

  def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] =
    Future.successful(Status(statusCode)(message))

  def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    implicit val mapContext: MapContext = RequestMapContext.extractMapContext(request)

    Try {
      val usefulException = HttpErrorHandlerExceptions
        .throwableToUsefulException(None, isProd, exception)

      val result =
        if (isProd) InternalServerError
        else InternalServerError("A server error occurred: " + exception.getMessage)

      putMapContext(result)

      logServerError(request, usefulException)

      result
    }.fold(
      ex => {
        val result = InternalServerError

        putMapContext(result)

        logger.error("Error while handling error", ex)

        Future.successful(result)
      },
      result => Future.successful(result)
    )
  }

  private def putMapContext(result: Result)(implicit
    mapContext: MapContext
  ) = mapContext.put(
    "response_status" -> result.header.status.toLong,
    "response_length" -> result.body.contentLength.getOrElse(0L)
  )

  private def logServerError(request: RequestHeader, usefulException: UsefulException)(implicit
    mapContext: MapContext
  ): Unit = logger.error(
    """
      |
      |! @%s - Internal server error, for (%s) [%s] ->
      | """.stripMargin.format(usefulException.id, request.method, request.uri),
    usefulException
  )

}
