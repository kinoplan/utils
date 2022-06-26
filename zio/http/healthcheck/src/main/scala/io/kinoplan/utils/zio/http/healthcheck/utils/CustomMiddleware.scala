package io.kinoplan.utils.zio.http.healthcheck.utils

import java.io.IOException

import zhttp.http._
import zhttp.http.middleware.HttpMiddleware
import zio.{Clock, ZIO}

object CustomMiddleware {

  final def debug: HttpMiddleware[Clock, IOException] = Middleware.interceptZIOPatch(req =>
    Clock.nanoTime.flatMap(start =>
      ZIO.logInfo(s"[START] ${req.method} ${req.url.encode}").as((req.method, req.url, start))
    )
  ) {
    case (response, (method, url, start)) => for {
        end <- Clock.nanoTime
        _ <- ZIO.logInfo(
          s"[END] $method ${url.encode} " +
            s"took ${(end - start) / 1000000}ms and returned ${response.status.asJava.code()}"
        )
      } yield Patch.empty
  }

}
