package io.kinoplan.utils.zio.redisson.config.extensions

import org.redisson.config.SslProvider

sealed trait SslProviderType {
  def sslProvider: SslProvider
}

object SslProviderType {

  case object JDK extends SslProviderType {
    override def sslProvider: SslProvider = SslProvider.JDK
  }

  case object OPENSSL extends SslProviderType {
    override def sslProvider: SslProvider = SslProvider.OPENSSL
  }

}
