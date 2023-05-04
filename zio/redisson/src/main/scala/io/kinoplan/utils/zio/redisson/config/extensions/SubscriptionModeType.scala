package io.kinoplan.utils.zio.redisson.config.extensions

import org.redisson.config.SubscriptionMode

sealed trait SubscriptionModeType {
  def value: SubscriptionMode
}

object SubscriptionModeType {

  case object SLAVE extends SubscriptionModeType {
    override def value: SubscriptionMode = SubscriptionMode.SLAVE
  }

  case object MASTER extends SubscriptionModeType {
    override def value: SubscriptionMode = SubscriptionMode.MASTER
  }

}
