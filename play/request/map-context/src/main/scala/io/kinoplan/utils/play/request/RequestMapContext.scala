package io.kinoplan.utils.play.request

import play.api.libs.typedmap.TypedKey
import play.api.mvc.RequestHeader
import play.api.mvc.request.RequestAttrKey

import io.kinoplan.utils.scala.logging.context.MapContext

trait RequestMapContext {

  object Headers {
    val RequestIdHeader = "X-Request-Id"
    val RequestRealIp = "X-Real-IP"
  }

  object Keys {
    val MapContextTypedKey: TypedKey[MapContext] = TypedKey[MapContext]
    val RequestId = "request_id"
    val RequestInternalId = "request_internal_id"
    val RequestRemoteAddress = "request_remote_address"
    val RequestRealIp = "request_real_ip"
    val RequestMethod = "request_method"
    val RequestPath = "request_path"
    val RequestHost = "request_host"
  }

  implicit class RequestMapContextExtended(request: RequestHeader) {

    def putMapContext(pairs: (String, Any)*): MapContext = {
      val mapContext = extractMapContext(request).put(pairs: _*)

      request.addAttr(Keys.MapContextTypedKey, mapContext)

      mapContext
    }

  }

  implicit def extractMapContext(implicit
    request: RequestHeader
  ): MapContext = {
    import request._

    request
      .attrs
      .get(Keys.MapContextTypedKey)
      .getOrElse {
        val internalRequestId = attrs.get(RequestAttrKey.Id).getOrElse(0L).toHexString.toUpperCase
        val requestId = headers.get(Headers.RequestIdHeader).getOrElse(internalRequestId)
        val realIp = request.headers.get(Headers.RequestRealIp).getOrElse(request.remoteAddress)

        MapContext(
          Map(
            Keys.RequestId -> requestId,
            Keys.RequestInternalId -> internalRequestId,
            Keys.RequestRemoteAddress -> remoteAddress,
            Keys.RequestRealIp -> realIp,
            Keys.RequestMethod -> method,
            Keys.RequestPath -> path,
            Keys.RequestHost -> host
          ) ++
            request
              .queryString
              .collect {
                case (key, values) if values.nonEmpty =>
                  s"request_param_${key.toLowerCase}" -> values.head
              }
        )
      }
  }

}

object RequestMapContext extends RequestMapContext
