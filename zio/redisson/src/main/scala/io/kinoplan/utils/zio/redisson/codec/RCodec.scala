package io.kinoplan.utils.zio.redisson.codec

import org.redisson.client.codec._

case class RCodec private (underlying: Option[Codec])

object RCodec {
  implicit val dummyCodec: RCodec = RCodec(None)

  val stringCodec: RCodec = RCodec.create(StringCodec.INSTANCE)
  val intCodec: RCodec = RCodec.create(IntegerCodec.INSTANCE)
  val longCodec: RCodec = RCodec.create(LongCodec.INSTANCE)
  val doubleCodec: RCodec = RCodec.create(IntegerCodec.INSTANCE)
  val byteArrayCodec: RCodec = RCodec.create(ByteArrayCodec.INSTANCE)

  def create(codec: Codec): RCodec = RCodec(Some(codec))
}
