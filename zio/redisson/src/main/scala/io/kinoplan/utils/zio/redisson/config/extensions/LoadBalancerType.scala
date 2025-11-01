package io.kinoplan.utils.zio.redisson.config.extensions

import org.redisson.connection.balancer._

import io.kinoplan.utils.cross.collection.MapSyntax.syntaxMapOps
import io.kinoplan.utils.cross.collection.converters._

sealed private[redisson] trait LoadBalancerType {
  def underlying: LoadBalancer
}

private[redisson] object LoadBalancerType {

  case object CommandsLoadBalancer extends LoadBalancerType {

    override def underlying: LoadBalancer =
      new org.redisson.connection.balancer.CommandsLoadBalancer()

  }

  case class WeightedRoundRobinBalancer(weights: Map[String, Int], defaultWeight: Int)
      extends LoadBalancerType {

    override def underlying: LoadBalancer =
      new org.redisson.connection.balancer.WeightedRoundRobinBalancer(
        weights.crossMapValues(int2Integer).asJava,
        defaultWeight
      )

  }

  case object RoundRobinLoadBalancer extends LoadBalancerType {

    override def underlying: LoadBalancer =
      new org.redisson.connection.balancer.RoundRobinLoadBalancer()

  }

  case object RandomLoadBalancer extends LoadBalancerType {

    override def underlying: LoadBalancer = new org.redisson.connection.balancer.RandomLoadBalancer()

  }

}
