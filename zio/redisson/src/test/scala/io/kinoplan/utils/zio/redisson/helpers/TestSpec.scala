package io.kinoplan.utils.zio.redisson.helpers

import zio.ZIO

case class TestSpec[R, E, A](label: String, result: ZIO[R, E, A])
