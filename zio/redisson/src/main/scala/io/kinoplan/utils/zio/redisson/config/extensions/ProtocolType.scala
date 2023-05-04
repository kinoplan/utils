package io.kinoplan.utils.zio.redisson.config.extensions

import org.redisson.config.Protocol

sealed private[redisson] trait ProtocolType {
  def underlying: Protocol
}

private[redisson] object ProtocolType {

  case object RESP2 extends ProtocolType {
    override def underlying: Protocol = Protocol.RESP2
  }

  case object RESP3 extends ProtocolType {
    override def underlying: Protocol = Protocol.RESP3
  }

}
