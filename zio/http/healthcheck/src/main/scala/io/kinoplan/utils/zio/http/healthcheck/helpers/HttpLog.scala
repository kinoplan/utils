package io.kinoplan.utils.zio.http.healthcheck.helpers

import java.util.concurrent.TimeUnit

import zhttp.http.{Http, HttpApp, Request, Response}
import zio.ZIO
import zio.clock.{Clock, currentTime}
import zio.logging.{Logging, log}

object HttpLog {

  def apply[R <: Logging with Clock, E](httpApp: HttpApp[R, E]): HttpApp[R, E] = Http.flatten {
    Http.fromEffectFunction[Request] { request =>
      for {
        start <- currentTime(TimeUnit.MILLISECONDS)
        _ <- log.info(s"[START] ${request.method} ${request.url.asString}")
      } yield httpApp.mapM { response =>
        for {
          end <- currentTime(TimeUnit.MILLISECONDS)
          _ <- response match {
            case response: Response.HttpResponse[R, E] => log.info(
                s"[END] ${request.method} ${request.url.asString} " +
                  s"took ${end - start}ms and returned ${response.status.toJHttpStatus}"
              )
            case _ => ZIO.unit
          }
        } yield response
      }
    }
  }

}
