package io.kinoplan.utils.zio.redisson.config.extensions

import org.redisson.config.ReadMode

sealed private[redisson] trait ReadModeType {
  def underlying: ReadMode
}

private[redisson] object ReadModeType {

  case object SLAVE extends ReadModeType {
    override def underlying: ReadMode = ReadMode.SLAVE
  }

  case object MASTER extends ReadModeType {
    override def underlying: ReadMode = ReadMode.MASTER
  }

  case object MASTER_SLAVE extends ReadModeType {
    override def underlying: ReadMode = ReadMode.MASTER_SLAVE
  }

}
