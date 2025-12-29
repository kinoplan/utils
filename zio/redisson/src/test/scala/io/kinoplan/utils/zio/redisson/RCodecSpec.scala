package io.kinoplan.utils.zio.redisson

import org.redisson.client.codec._
import org.redisson.codec.KryoCodec
import zio.Scope
import zio.test._

import io.kinoplan.utils.zio.redisson.codec.RCodec

object RCodecSpec extends ZIOSpecDefault {

  private val kryoCodec = new KryoCodec()

  override def spec: Spec[TestEnvironment with Scope, Throwable] = suite("RCodec")(
    test("check stringCodec")(assertTrue(RCodec.stringCodec.underlying.contains(StringCodec.INSTANCE))),
    test("check intCodec")(assertTrue(RCodec.intCodec.underlying.contains(IntegerCodec.INSTANCE))),
    test("check longCodec")(assertTrue(RCodec.longCodec.underlying.contains(LongCodec.INSTANCE))),
    test("check doubleCodec")(assertTrue(RCodec.doubleCodec.underlying.contains(DoubleCodec.INSTANCE))),
    test("check byteArrayCodec")(
      assertTrue(RCodec.byteArrayCodec.underlying.contains(ByteArrayCodec.INSTANCE))
    ),
    test("check create codec")(assertTrue(RCodec.create(kryoCodec).underlying.contains(kryoCodec))),
    test("check create empty codec")(assertTrue(RCodec.create().underlying.isEmpty))
  ) @@ redissonTestAspect()

}
