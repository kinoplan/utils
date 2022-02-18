package io.kinoplan.utils.zio.http.healthcheck.helpers

import java.util.concurrent.TimeUnit

import zhttp.http.{Http, HttpApp, Request}
import zio.clock.{Clock, currentTime}
import zio.logging.{Logging, log}

object HttpLog {

  def apply[R <: Logging with Clock, E](httpApp: HttpApp[R, E]): HttpApp[R, E] = Http.flatten {
    Http.collectZIO[Request] { case request =>
      for {
        start <- currentTime(TimeUnit.MILLISECONDS)
        _ <- log.info(s"[START] ${request.method} ${request.url.path.encode}")
      } yield httpApp.mapZIO { response =>
        for {
          end <- currentTime(TimeUnit.MILLISECONDS)
          _ <- log.info(
            s"[END] ${request.method} ${request.url.encode} " +
              s"took ${end - start}ms and returned ${response.status.asJava.code}"
          )
        } yield response
      }
    }
  }

}
