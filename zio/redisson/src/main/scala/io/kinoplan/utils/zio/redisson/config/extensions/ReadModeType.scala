package io.kinoplan.utils.zio.redisson.config.extensions

import org.redisson.config.ReadMode

sealed trait ReadModeType {
  def value: ReadMode
}

object ReadModeType {

  case object SLAVE extends ReadModeType {
    override def value: ReadMode = ReadMode.SLAVE
  }

  case object MASTER extends ReadModeType {
    override def value: ReadMode = ReadMode.MASTER
  }

  case object MASTER_SLAVE extends ReadModeType {
    override def value: ReadMode = ReadMode.MASTER_SLAVE
  }

}
