package io.kinoplan.utils.zio.redisson.codec

import org.redisson.client.codec.Codec

case class RCodec private (underlying: Option[Codec])

object RCodec {
  implicit val dummyCodec: RCodec = RCodec(None)

  def create(codec: Codec): RCodec = RCodec(Some(codec))
}
