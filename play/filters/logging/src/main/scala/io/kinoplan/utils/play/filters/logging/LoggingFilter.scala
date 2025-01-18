package io.kinoplan.utils.play.filters.logging

import javax.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

import org.slf4j.MarkerFactory
import play.api.{ConfigLoader, Configuration, MarkerContext}
import play.api.http.{MimeTypes, Status}
import play.api.mvc._

import io.kinoplan.utils.play.compat.Materializer
import io.kinoplan.utils.play.request.RequestMapContext
import io.kinoplan.utils.scala.logging.Loggable
import io.kinoplan.utils.scala.logging.context.MapContext

@Singleton
class LoggingFilter @Inject() (configuration: Configuration)(implicit
  val mat: Materializer,
  ec: ExecutionContext
) extends Filter
      with Loggable {

  case class Config(response: Response)
  case class Response(body: Body)

  case class Body(
    maxLength: Int,
    logSuccessful: Boolean,
    logClientError: Boolean,
    logServerError: Boolean
  )

  object Body {

    implicit val configLoader: ConfigLoader[Body] = (_, path: String) =>
      Body(
        maxLength = configuration.getOptional[Int](s"$path.maxLength").getOrElse(1500),
        logSuccessful = configuration.getOptional[Boolean](s"$path.logSuccessful").getOrElse(false),
        logClientError = configuration.getOptional[Boolean](s"$path.logClientError").getOrElse(true),
        logServerError = configuration.getOptional[Boolean](s"$path.logServerError").getOrElse(true)
      )

  }

  private val config =
    Config(Response(configuration.get[Body]("io.kinoplan.play.filters.logging.response.body")))

  implicit val markerContext: MarkerContext = MarkerFactory.getMarker("REQUEST")

  override def apply(
    next: RequestHeader => Future[Result]
  )(request: RequestHeader): Future[Result] = {
    val startTime = System.currentTimeMillis

    implicit val mapContext: MapContext = RequestMapContext.extractMapContext(request)
    val requestWithMapContext =
      request.addAttr(RequestMapContext.Keys.MapContextTypedKey, mapContext)

    logger.info(s"[START] ${requestWithMapContext.method} ${requestWithMapContext.uri}")

    next(request).flatMap { result =>
      val endTime = System.currentTimeMillis
      val requestTime = endTime - startTime
      val message = s"[END] ${requestWithMapContext.method} ${requestWithMapContext.uri} took ${
          requestTime
        }ms and returned ${result.header.status}"

      mapContext.put(
        "response_time" -> requestTime,
        "response_status" -> result.header.status.toLong,
        "response_length" -> result.body.contentLength.getOrElse(0L)
      )

      resultWithResponseBodyLogging(result, message)
    }
  }

  private def resultWithResponseBodyLogging(result: Result, logMessagePart: String)(implicit
    mapContext: MapContext
  ): Future[Result] = {
    import config.response.body._

    if (
      Status.isSuccessful(result.header.status) && logSuccessful ||
      Status.isClientError(result.header.status) && logClientError ||
      Status.isServerError(result.header.status) && logServerError
    ) result.body.contentType match {
      case Some(contentType) =>
        if (isLoggableResponse(contentType.trim)) result
          .body
          .consumeData
          .map { body =>
            val responseBody = body.decodeString("UTF-8")

            val message = s"$logMessagePart\nResponse: ${responseBody.take(maxLength)}" +
              (if (responseBody.length > maxLength)
                 "...\nNote: The log message is limited by the " +
                   s"io.kinoplan.play.filters.logging.response.body.maxLength=$maxLength configuration parameter. " +
                   "Set a higher value for the maxLength parameter if you want to see more."
               else "")

            logResponse(result, message)

            result
          }
        else {
          logResponse(result, s"$logMessagePart\nContent type $contentType")

          Future.successful(result)
        }
      case _ =>
        logResponse(result, s"$logMessagePart\nNo content type")

        Future.successful(result)
    }
    else {
      logResponse(result, logMessagePart)

      Future.successful(result)
    }
  }

  private def isLoggableResponse(contentType: String) = contentType.contains(MimeTypes.JSON) ||
    contentType.contains(MimeTypes.TEXT) || contentType.contains(MimeTypes.XML)

  private def logResponse(result: Result, message: String)(implicit
    mapContext: MapContext
  ): Unit =
    if (Status.isServerError(result.header.status)) logger.error(message)
    else if (Status.isClientError(result.header.status)) logger.warn(message)
    else logger.info(message)

}
