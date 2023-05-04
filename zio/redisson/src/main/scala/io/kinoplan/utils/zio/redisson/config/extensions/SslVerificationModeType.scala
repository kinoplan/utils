package io.kinoplan.utils.zio.redisson.config.extensions

import org.redisson.config.SslVerificationMode

sealed private[redisson] trait SslVerificationModeType {
  def underlying: SslVerificationMode
}

private[redisson] object SslVerificationModeType {

  case object NONE extends SslVerificationModeType {
    override def underlying: SslVerificationMode = SslVerificationMode.NONE
  }

  case object CA_ONLY extends SslVerificationModeType {
    override def underlying: SslVerificationMode = SslVerificationMode.CA_ONLY
  }

  case object STRICT extends SslVerificationModeType {
    override def underlying: SslVerificationMode = SslVerificationMode.STRICT
  }

}
