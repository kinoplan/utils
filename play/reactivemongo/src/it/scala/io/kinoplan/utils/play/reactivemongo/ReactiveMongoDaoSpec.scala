package io.kinoplan.utils.play.reactivemongo

import com.dimafeng.testcontainers.{ForAllTestContainer, MongoDBContainer}
import org.scalatest.flatspec.AnyFlatSpec
import org.testcontainers.utility.DockerImageName
import play.api.Logger

class ReactiveMongoDaoSpec extends AnyFlatSpec with ForAllTestContainer {
  val logger: Logger = Logger("application")

  // deprecated implicit conversion
  override val container = MongoDBContainer(DockerImageName.parse("mongo:3.4.24"))
//    .container
//    .withExposedPorts(27017)
  // .asInstanceOf[Container]
  container.start()

  container.container.withExposedPorts(27017)

  logger.info(s"MONGODB: ${container.container.getConnectionString}")

  container.stop()

}
