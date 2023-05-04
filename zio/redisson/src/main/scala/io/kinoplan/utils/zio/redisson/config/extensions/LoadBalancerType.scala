package io.kinoplan.utils.zio.redisson.config.extensions

import io.kinoplan.utils.cross.collection.MapSyntax.syntaxMapOps
import org.redisson.connection.balancer._

import scala.jdk.CollectionConverters.MapHasAsJava

sealed trait LoadBalancerType {
  def loadBalancer: LoadBalancer
}

object LoadBalancerType {

  case object CommandsLoadBalancer extends LoadBalancerType {

    override def loadBalancer: LoadBalancer =
      new org.redisson.connection.balancer.CommandsLoadBalancer()

  }

  case class WeightedRoundRobinBalancer(weights: Map[String, Int], defaultWeight: Int)
      extends LoadBalancerType {

    override def loadBalancer: LoadBalancer =
      new org.redisson.connection.balancer.WeightedRoundRobinBalancer(
        weights.crossMapValues(int2Integer).asJava,
        defaultWeight
      )

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
