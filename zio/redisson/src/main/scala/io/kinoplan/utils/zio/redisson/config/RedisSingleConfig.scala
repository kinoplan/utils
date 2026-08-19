package io.kinoplan.utils.zio.redisson.config

import java.net.URL

import org.redisson.config.Config
import zio.{Layer, ZIO, ZLayer}
import zio.Config.Error
import zio.config.magnolia.{DeriveConfig, deriveConfig}

import io.kinoplan.utils.zio.redisson.utils.IdentitySyntax.syntaxIdentityOps

private[redisson] case class RedisSingleConfig(
  host: String,
  port: Int,
  protocol: Option[String],
  subscriptionConnectionMinimumIdleSize: Option[Int],
  subscriptionConnectionPoolSize: Option[Int],
  connectionMinimumIdleSize: Int = 1,
  connectionPoolSize: Option[Int],
  dnsMonitoringInterval: Option[Long],
  dnsMonitoringTimes: Option[Int],
  idleConnectionTimeout: Option[Int],
  connectTimeout: Option[Int],
  timeout: Option[Int],
  retryAttempts: Option[Int],
  database: Option[Int],
  subscriptionsPerConnection: Option[Int],
  subscriptionTimeout: Option[Int],
  clientName: Option[String],
  pingConnectionInterval: Option[Int]
) {

  def redissonConfig(config: Config): Config = {
    val singleProtocol = protocol.getOrElse("redis://")
    val address = s"$singleProtocol$host:$port"

    config
      .useSingleServer()
      .setAddress(address)
      .applyOption(subscriptionConnectionMinimumIdleSize)((self, value) =>
        self.setSubscriptionConnectionMinimumIdleSize(value)
      )
      .applyOption(subscriptionConnectionPoolSize)((self, value) =>
        self.setSubscriptionConnectionPoolSize(value)
      )
      .setConnectionMinimumIdleSize(connectionMinimumIdleSize)
      .applyOption(connectionPoolSize)((self, value) => self.setConnectionPoolSize(value))
      .applyOption(dnsMonitoringInterval)((self, value) => self.setDnsMonitoringInterval(value))
      .applyOption(dnsMonitoringTimes)((self, value) => self.setDnsMonitoringTimes(value))
      .applyOption(idleConnectionTimeout)((self, value) => self.setIdleConnectionTimeout(value))
      .applyOption(connectTimeout)((self, value) => self.setConnectTimeout(value))
      .applyOption(timeout)((self, value) => self.setTimeout(value))
      .applyOption(retryAttempts)((self, value) => self.setRetryAttempts(value))
      .applyOption(database)((self, value) => self.setDatabase(value))
      .applyOption(subscriptionsPerConnection)((self, value) =>
        self.setSubscriptionsPerConnection(value)
      )
      .applyOption(subscriptionTimeout)((self, value) => self.setSubscriptionTimeout(value))
      .applyOption(clientName)((self, value) => self.setClientName(value))
      .applyOption(pingConnectionInterval)((self, value) => self.setPingConnectionInterval(value))

    config
  }

}

private[redisson] object RedisSingleConfig {
  implicit val deriveURL: DeriveConfig[URL] = DeriveConfig[String].map(new URL(_))

  private val config = deriveConfig[RedisSingleConfig].nested("redis", "single")

  val live: Layer[Error, RedisSingleConfig] = ZLayer.fromZIO(ZIO.config(config))
}
