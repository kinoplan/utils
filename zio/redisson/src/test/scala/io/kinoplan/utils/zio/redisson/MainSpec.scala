package io.kinoplan.utils.zio.redisson

import zio.Scope
import zio.test.{Spec, TestAspect, TestEnvironment, ZIOSpecDefault}

object MainSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Throwable] = suite("ZIO Redisson Suites")(
    RCodecSpec.spec,
    RedisClientSpec.spec,
    RedissonNativeSpec.spec,
    RedissonSentinelSpec.spec,
    RedissonSingleSpec.spec
  ) @@ TestAspect.sequential

}
