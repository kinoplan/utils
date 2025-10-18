package io.kinoplan.utils.zio.redisson.config

import java.net.URL

import org.redisson.client.FailedConnectionDetector
import org.redisson.config.Config
import zio.{Layer, ZIO, ZLayer}
import zio.Config.Error
import zio.config.magnolia._

import io.kinoplan.utils.zio.redisson.config.extensions._
import io.kinoplan.utils.zio.redisson.utils.IdentitySyntax.syntaxIdentityOps

private[redisson] case class RedisSentinelConfig(
  host: Option[String],
  port: Option[Int],
  protocol: Option[String],
  addresses: Set[String] = Set.empty,
  masterName: String,
  checkSentinelsList: Boolean = false, // defaults false because unsafe
  readMode: ReadModeType = ReadModeType.MASTER,
  subscriptionMode: SubscriptionModeType = SubscriptionModeType.MASTER,
  masterConnectionMinimumIdleSize: Int = 1,
  slaveConnectionMinimumIdleSize: Int = 1,
  dnsMonitoringInterval: Option[Long],
  checkSlaveStatusWithSyncing: Option[Boolean],
  loadBalancer: Option[LoadBalancerType],
  subscriptionConnectionMinimumIdleSize: Option[Int],
  subscriptionConnectionPoolSize: Option[Int],
  masterConnectionPoolSize: Option[Int],
  slaveConnectionPoolSize: Option[Int],
  idleConnectionTimeout: Option[Int],
  connectTimeout: Option[Int],
  timeout: Option[Int],
  retryAttempts: Option[Int],
  failedSlaveReconnectionInterval: Option[Int],
  failedSlaveNodeDetector: Option[Long],
  database: Option[Int],
  password: Option[String],
  username: Option[String],
  sentinelPassword: Option[String],
  sentinelUsername: Option[String],
  sentinelsDiscovery: Option[Boolean],
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
    val sentinelAddresses = (
      for {
        sentinelHost <- host
        sentinelPort <- port
        sentinelProtocol = protocol.getOrElse("redis://")
      } yield s"$sentinelProtocol$sentinelHost:$sentinelPort"
    ).map(Set(_)).getOrElse(addresses).toSeq

    config
      .useSentinelServers()
      .addSentinelAddress(sentinelAddresses: _*)
      .setMasterName(masterName)
      .setCheckSentinelsList(checkSentinelsList)
      .setReadMode(readMode.underlying)
      .setSubscriptionMode(subscriptionMode.underlying)
      .setMasterConnectionMinimumIdleSize(masterConnectionMinimumIdleSize)
      .setSlaveConnectionMinimumIdleSize(slaveConnectionMinimumIdleSize)
      .applyOption(dnsMonitoringInterval)((self, value) => self.setDnsMonitoringInterval(value))
      .applyOption(checkSlaveStatusWithSyncing)((self, value) =>
        self.setCheckSlaveStatusWithSyncing(value)
      )
      .applyOption(loadBalancer)((self, value) => self.setLoadBalancer(value.underlying))
      .applyOption(subscriptionConnectionMinimumIdleSize)((self, value) =>
        self.setSubscriptionConnectionMinimumIdleSize(value)
      )
      .applyOption(subscriptionConnectionPoolSize)((self, value) =>
        self.setSubscriptionConnectionPoolSize(value)
      )
      .applyOption(masterConnectionPoolSize)((self, value) => self.setMasterConnectionPoolSize(value))
      .applyOption(slaveConnectionPoolSize)((self, value) => self.setSlaveConnectionPoolSize(value))
      .applyOption(idleConnectionTimeout)((self, value) => self.setIdleConnectionTimeout(value))
      .applyOption(connectTimeout)((self, value) => self.setConnectTimeout(value))
      .applyOption(timeout)((self, value) => self.setTimeout(value))
      .applyOption(retryAttempts)((self, value) => self.setRetryAttempts(value))
      .applyOption(failedSlaveReconnectionInterval)((self, value) =>
        self.setFailedSlaveReconnectionInterval(value)
      )
      .applyOption(failedSlaveNodeDetector)((self, value) =>
        self.setFailedSlaveNodeDetector(new FailedConnectionDetector(value))
      )
      .applyOption(database)((self, value) => self.setDatabase(value))
      .applyOption(password)((self, value) => self.setPassword(value))
      .applyOption(username)((self, value) => self.setUsername(value))
      .applyOption(sentinelPassword)((self, value) => self.setSentinelPassword(value))
      .applyOption(sentinelUsername)((self, value) => self.setSentinelUsername(value))
      .applyOption(sentinelsDiscovery)((self, value) => self.setSentinelsDiscovery(value))
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

private[redisson] object RedisSentinelConfig {
  implicit val deriveURL: DeriveConfig[URL] = DeriveConfig[String].map(new URL(_))

  private val config = deriveConfig[RedisSentinelConfig].nested("redis", "sentinel")

  val live: Layer[Error, RedisSentinelConfig] = ZLayer.fromZIO(ZIO.config(config))
}
