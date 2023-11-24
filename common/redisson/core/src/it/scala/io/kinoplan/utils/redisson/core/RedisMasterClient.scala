package io.kinoplan.utils.redisson.core

import io.kinoplan.utils.redisson.core.client.RedisMasterClientBase

import scala.concurrent.{ExecutionContext, Future}

class RedisMasterClient(hostParam: String, portParam: Int) extends RedisMasterClientBase {

  implicit override protected val executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.global

  override protected val host: String = hostParam
  override protected val port: Int = portParam

  def ping(): Future[Boolean] = pingSingleMaster()

  def rPush(key: String, value: String): Future[Int] = super.rPush(key, value)

  def rPop(key: String): Future[Option[String]] = super.rPop[String](key)
}
