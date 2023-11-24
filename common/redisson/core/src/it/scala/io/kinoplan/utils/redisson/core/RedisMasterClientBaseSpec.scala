package io.kinoplan.utils.redisson.core

import com.dimafeng.testcontainers.{
  DockerComposeContainer,
  ExposedService,
  ForAllTestContainer,
  WaitingForService
}
import org.scalatest.wordspec.AsyncWordSpec
import org.testcontainers.containers.wait.strategy.Wait

import java.io.File
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class RedisMasterClientBaseSpec extends AsyncWordSpec with ForAllTestContainer {
  val host = "redis"
  val port = 6379

  override val container: DockerComposeContainer = DockerComposeContainer(
    new File(getClass.getClassLoader.getResource("docker-compose.yml").getPath),
    waitingFor =
      Some(WaitingForService("redis", Wait.forLogMessage(".*Ready to accept connections\\n", 1))),
    exposedServices = Seq(ExposedService(host, port))
  )

  lazy val client =
    new RedisMasterClient(container.getServiceHost(host, port), container.getServicePort(host, port))

  "RedisMasterClientBase" should {
    "ping" in client.ping().map(result => assert(result))

    "rPush/rPop" in {
      val key = "foo"
      val value = "bar"

      println(s"Host: ${container.getServiceHost(host, port)}")
      println(s"Port: ${container.getServicePort(host, port)}")

      val future = for {
        result <- client.rPush(key, value)
//        resultO <- client.rPop1(key)
      } yield result

      val result = Await.result(future, 3000.seconds)

      assert(result == 0)
    }
  }

}
