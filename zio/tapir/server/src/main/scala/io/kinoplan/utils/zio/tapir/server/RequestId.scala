package io.kinoplan.utils.zio.tapir.server

import sttp.tapir.model.ServerRequest
import sttp.tapir.server.interceptor.{RequestInterceptor, RequestResult}
import sttp.tapir.server.interceptor.RequestInterceptor.RequestResultEffectTransform
import zio.{RIO, Random, ZIO}

object RequestId {
  private val requestIdHeader = "X-Request-ID"
  private val requestIdAnnotation = "request_id"

  def interceptor[R](
    headerName: String = requestIdHeader,
    annotationName: String = requestIdAnnotation
  ) = RequestInterceptor.transformResultEffect(
    new RequestResultEffectTransform[RIO[R, *]] {

      override def apply[B](
        request: ServerRequest,
        result: RIO[R, RequestResult[B]]
      ): RIO[R, RequestResult[B]] = ZIO
        .fromOption(request.header(headerName))
        .orElse(Random.nextUUID.map(_.toString))
        .flatMap(value => ZIO.logAnnotate(annotationName, value)(result))

    }
  )

}
