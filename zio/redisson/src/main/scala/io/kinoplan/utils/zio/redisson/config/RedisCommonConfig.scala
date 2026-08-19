package io.kinoplan.utils.zio.redisson.config

import java.net.URL

import org.redisson.config.Config
import zio.{Layer, ZIO, ZLayer}
import zio.Config.Error
import zio.config.magnolia.{DeriveConfig, deriveConfig}

import io.kinoplan.utils.zio.redisson.config.extensions._
import io.kinoplan.utils.zio.redisson.utils.IdentitySyntax.syntaxIdentityOps

private[redisson] case class RedisCommonConfig(
  password: Option[String],
  username: Option[String],
  tcpKeepAlive: Option[Boolean],
  tcpNoDelay: Option[Boolean],
  lazyInitialization: Option[Boolean],
  nettyThreads: Option[Int],
  transportMode: Option[TransportModeType],
  threads: Option[Int],
  protocol: Option[ProtocolType],
  lockWatchdogTimeout: Option[Long],
  lockWatchdogBatchSize: Option[Int],
  checkLockSyncedSlaves: Option[Boolean],
  slavesSyncTimeout: Option[Long],
  reliableTopicWatchdogTimeout: Option[Long],
  useScriptCache: Option[Boolean],
  keepPubSubOrder: Option[Boolean],
  minCleanUpDelay: Option[Int],
  maxCleanUpDelay: Option[Int],
  cleanUpKeysAmount: Option[Int],
  useThreadClassLoader: Option[Boolean],
  sslProtocols: Option[Seq[String]],
  sslVerificationMode: Option[SslVerificationModeType],
  sslProvider: Option[SslProviderType],
  sslTruststore: Option[URL],
  sslTruststorePassword: Option[String],
  sslKeystoreType: Option[String],
  sslKeystore: Option[URL],
  sslKeystorePassword: Option[String]
) {

  def redissonConfig: Config = {
    val config = new Config()

    config
      .applyOption(password)((self, value) => self.setPassword(value))
      .applyOption(username)((self, value) => self.setUsername(value))
      .applyOption(tcpKeepAlive)((self, value) => self.setTcpKeepAlive(value))
      .applyOption(tcpNoDelay)((self, value) => self.setTcpNoDelay(value))
      .applyOption(lazyInitialization)((self, value) => self.setLazyInitialization(value))
      .applyOption(nettyThreads)((self, value) => self.setNettyThreads(value))
      .applyOption(transportMode)((self, value) => self.setTransportMode(value.underlying))
      .applyOption(threads)((self, value) => self.setThreads(value))
      .applyOption(protocol)((self, value) => self.setProtocol(value.underlying))
      .applyOption(lockWatchdogTimeout)((self, value) => self.setLockWatchdogTimeout(value))
      .applyOption(lockWatchdogBatchSize)((self, value) => self.setLockWatchdogBatchSize(value))
      .applyOption(checkLockSyncedSlaves)((self, value) => self.setCheckLockSyncedSlaves(value))
      .applyOption(slavesSyncTimeout)((self, value) => self.setSlavesSyncTimeout(value))
      .applyOption(reliableTopicWatchdogTimeout)((self, value) =>
        self.setReliableTopicWatchdogTimeout(value)
      )
      .applyOption(useScriptCache)((self, value) => self.setUseScriptCache(value))
      .applyOption(keepPubSubOrder)((self, value) => self.setKeepPubSubOrder(value))
      .applyOption(minCleanUpDelay)((self, value) => self.setMinCleanUpDelay(value))
      .applyOption(maxCleanUpDelay)((self, value) => self.setMaxCleanUpDelay(value))
      .applyOption(cleanUpKeysAmount)((self, value) => self.setCleanUpKeysAmount(value))
      .applyOption(useThreadClassLoader)((self, value) => self.setUseThreadClassLoader(value))
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

    config
  }

}

private[redisson] object RedisCommonConfig {
  implicit val deriveURL: DeriveConfig[URL] = DeriveConfig[String].map(new URL(_))

  private val config = deriveConfig[RedisCommonConfig].nested("redis")

  val live: Layer[Error, RedisCommonConfig] = ZLayer.fromZIO(ZIO.config(config))
}
