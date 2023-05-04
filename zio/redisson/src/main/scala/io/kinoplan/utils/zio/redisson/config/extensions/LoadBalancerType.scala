package io.kinoplan.utils.zio.redisson.config.extensions

import org.redisson.connection.balancer._
import io.kinoplan.utils.redisson.crossCollectionConverters._

sealed trait LoadBalancerType {
  def loadBalancer: LoadBalancer
}

object LoadBalancerType {

  case object CommandsLoadBalancer extends LoadBalancerType {

    override def loadBalancer: LoadBalancer =
      new org.redisson.connection.balancer.CommandsLoadBalancer()

  }

  case class WeightedRoundRobinBalancer(weights: Map[String, Integer], defaultWeight: Int)
      extends LoadBalancerType {

    override def loadBalancer: LoadBalancer =
      new org.redisson.connection.balancer.WeightedRoundRobinBalancer(weights.asJava, defaultWeight)

  }

  case object RoundRobinLoadBalancer extends LoadBalancerType {

    override def loadBalancer: LoadBalancer =
      new org.redisson.connection.balancer.RoundRobinLoadBalancer()

  }

  case object RandomLoadBalancer extends LoadBalancerType {

    override def loadBalancer: LoadBalancer =
      new org.redisson.connection.balancer.RandomLoadBalancer()

  }

}
