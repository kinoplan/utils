package io.kinoplan.utils.zio.monitoring

import zio.{Task, ZIOAppDefault}

object PrometheusServerApp extends ZIOAppDefault {
  def run: Task[Nothing] = PrometheusServer.start()
}
