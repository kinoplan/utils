package io.kinoplan.utils.zio.monitoring.prometheus.config

import com.typesafe.config.ConfigFactory
import zio.Layer
import zio.config._
import zio.config.ConfigDescriptor._
import zio.config.typesafe.TypesafeConfig

case class PrometheusConfig(port: Int)

object PrometheusConfig {
  private val configDescriptor = nested("prometheus")(int("port").to[PrometheusConfig])

  val live: Layer[ReadError[String], PrometheusConfig] =
    TypesafeConfig.fromTypesafeConfig(ConfigFactory.load.resolve, configDescriptor)

}
