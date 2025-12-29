package io.kinoplan.utils.zio.opentelemetry.config.extensions

sealed private[opentelemetry] trait TracerProviderType

private[opentelemetry] object TracerProviderType {
  case object noop extends TracerProviderType
  case object stdout extends TracerProviderType
  case object jaeger extends TracerProviderType
}
