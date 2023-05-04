package io.kinoplan.utils.zio.redisson.config

import java.net.URL

import org.redisson.config.Config
import zio.{Layer, ZIO, ZLayer}
import zio.Config.Error
import zio.config.magnolia.{DeriveConfig, deriveConfig}

import io.kinoplan.utils.zio.redisson.config.extensions._
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
  idleConnectionTimeout: Option[Int],
  connectTimeout: Option[Int],
  timeout: Option[Int],
  retryAttempts: Option[Int],
  database: Option[Int],
  password: Option[String],
  username: Option[String],
  subscriptionsPerConnection: Option[Int],
  subscriptionTimeout: Option[Int],
  clientName: Option[String],
  sslProtocols: Option[Seq[String]],
  sslVerificationMode: Option[SslVerificationModeType],
  sslProvider: Option[SslProviderType],
  sslTruststore: Option[URL],
  sslTruststorePassword: Option[String],
  sslKeystoreType: Option[String],
  sslKeystore: Option[URL],
  sslKeystorePassword: Option[String],
  pingConnectionInterval: Option[Int],
  keepAlive: Option[Boolean],
  tcpNoDelay: Option[Boolean]
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
      .applyOption(idleConnectionTimeout)((self, value) => self.setIdleConnectionTimeout(value))
      .applyOption(connectTimeout)((self, value) => self.setConnectTimeout(value))
      .applyOption(timeout)((self, value) => self.setTimeout(value))
      .applyOption(retryAttempts)((self, value) => self.setRetryAttempts(value))
      .applyOption(database)((self, value) => self.setDatabase(value))
      .applyOption(password)((self, value) => self.setPassword(value))
      .applyOption(username)((self, value) => self.setUsername(value))
      .applyOption(subscriptionsPerConnection)((self, value) =>
        self.setSubscriptionsPerConnection(value)
      )
      .applyOption(subscriptionTimeout)((self, value) => self.setSubscriptionTimeout(value))
      .applyOption(clientName)((self, value) => self.setClientName(value))
      .applyOption(sslProtocols)((self, value) => self.setSslProtocols(value.toArray))
      .applyOption(sslVerificationMode)((self, value) =>
        self.setSslVerificationMode(value.underlying)
      )
      .applyOption(sslProvider)((self, value) => self.setSslProvider(value.underlying))
      .applyOption(sslTruststore)((self, value) => self.setSslTruststore(value))
      .applyOption(sslTruststorePassword)((self, value) => self.setSslTruststorePassword(value))
      .applyOption(sslKeystoreType)((self, value) => self.setSslKeystoreType(value))
      .applyOption(sslKeystore)((self, value) => self.setSslKeystore(value))
      .applyOption(sslKeystorePassword)((self, value) => self.setSslKeystorePassword(value))
      .applyOption(pingConnectionInterval)((self, value) => self.setPingConnectionInterval(value))
      .applyOption(keepAlive)((self, value) => self.setKeepAlive(value))
      .applyOption(tcpNoDelay)((self, value) => self.setTcpNoDelay(value))

    config
  }

}

private[redisson] object RedisSingleConfig {
  implicit val deriveURL: DeriveConfig[URL] = DeriveConfig[String].map(new URL(_))

  private val config = deriveConfig[RedisSingleConfig].nested("redis", "single")

  val live: Layer[Error, RedisSingleConfig] = ZLayer.fromZIO(ZIO.config(config))
}
