package io.kinoplan.utils.zio.monitoring

import zio.{ExitCode, URIO, ZIOAppDefault}

object PrometheusServerApp extends ZIOAppDefault {
  def run: URIO[Any, ExitCode] = PrometheusServer.start().exitCode
}
