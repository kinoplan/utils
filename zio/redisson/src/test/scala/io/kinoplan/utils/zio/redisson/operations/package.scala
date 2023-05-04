package io.kinoplan.utils.zio.redisson

import zio._

import java.util.UUID

package object operations {
  val timeout: Duration = 1.seconds

  def generateKey: String = UUID.randomUUID().toString
}
