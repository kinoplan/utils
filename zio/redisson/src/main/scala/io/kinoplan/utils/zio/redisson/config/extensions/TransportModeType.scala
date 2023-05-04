package io.kinoplan.utils.zio.redisson.config.extensions

import org.redisson.config.TransportMode

sealed private[redisson] trait TransportModeType {
  def underlying: TransportMode
}

private[redisson] object TransportModeType {

  case object NIO extends TransportModeType {
    override def underlying: TransportMode = TransportMode.NIO
  }

  case object EPOLL extends TransportModeType {
    override def underlying: TransportMode = TransportMode.EPOLL
  }

  case object KQUEUE extends TransportModeType {
    override def underlying: TransportMode = TransportMode.KQUEUE
  }

  case object IO_URING extends TransportModeType {
    override def underlying: TransportMode = TransportMode.IO_URING
  }

}
