package io.kinoplan.utils.zio.opentelemetry.config.extensions

import enumeratum._

sealed private[opentelemetry] trait TracerProviderType extends EnumEntry

private[opentelemetry] object TracerProviderType extends Enum[TracerProviderType] {

  case object noop extends TracerProviderType
  case object stdout extends TracerProviderType
  case object jaeger extends TracerProviderType

  val values = findValues
}
