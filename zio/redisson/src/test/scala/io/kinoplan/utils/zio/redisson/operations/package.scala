package io.kinoplan.utils.zio.redisson

import java.util.UUID

import zio._

package object operations {
  val timeout: Duration = 1.seconds

  def generateKey: String = UUID.randomUUID().toString
}
