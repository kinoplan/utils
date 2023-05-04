package io.kinoplan.utils.zio.redisson

import java.util.UUID

package object operations {
  def generateKey: String = UUID.randomUUID().toString
}
