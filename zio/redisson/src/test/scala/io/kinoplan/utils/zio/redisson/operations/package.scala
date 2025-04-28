package io.kinoplan.utils.zio.redisson

import io.kinoplan.utils.zio.redisson.codec.RCodec
import org.redisson.client.codec.{ByteArrayCodec, StringCodec}
import zio._

import java.util.UUID

package object operations {
  val stringCodec: RCodec = RCodec.create(StringCodec.INSTANCE)
  val byteArrayCodec: RCodec = RCodec.create(ByteArrayCodec.INSTANCE)

  val timeout: Duration = 1.seconds

  def generateKey: String = UUID.randomUUID().toString
}
