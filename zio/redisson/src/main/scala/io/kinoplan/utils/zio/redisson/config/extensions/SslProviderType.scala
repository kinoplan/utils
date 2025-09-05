package io.kinoplan.utils.zio.redisson.config.extensions

import org.redisson.config.SslProvider

sealed private[redisson] trait SslProviderType {
  def underlying: SslProvider
}

private[redisson] object SslProviderType {

  case object JDK extends SslProviderType {
    override def underlying: SslProvider = SslProvider.JDK
  }

  case object OPENSSL extends SslProviderType {
    override def underlying: SslProvider = SslProvider.OPENSSL
  }

}
