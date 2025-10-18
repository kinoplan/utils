package io.kinoplan.utils.zio.redisson.config.extensions

import org.redisson.config.SubscriptionMode

sealed private[redisson] trait SubscriptionModeType {
  def underlying: SubscriptionMode
}

private[redisson] object SubscriptionModeType {

  case object SLAVE extends SubscriptionModeType {
    override def underlying: SubscriptionMode = SubscriptionMode.SLAVE
  }

  case object MASTER extends SubscriptionModeType {
    override def underlying: SubscriptionMode = SubscriptionMode.MASTER
  }

}
