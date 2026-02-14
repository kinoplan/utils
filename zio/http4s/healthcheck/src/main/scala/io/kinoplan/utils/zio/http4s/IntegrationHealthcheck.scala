package io.kinoplan.utils.zio.http4s

import org.http4s._
import org.http4s.dsl.Http4sDsl
import zio._
import zio.interop.catz._

import io.kinoplan.utils.IntegrationCheck
import io.kinoplan.utils.zio.http4s.config.IntegrationHealthcheckConfig

class IntegrationHealthcheck[R <: IntegrationHealthcheck.Env]
    extends Http4sDsl[RIO[R, *]] {

  def routes(
    additionalIntegrationChecks: Set[IntegrationCheck[Task]] = Set.empty
  ): HttpRoutes[RIO[R, *]] = HttpRoutes.of[RIO[R, *]] { case GET -> Root / "health" =>
    (
      for {
        config <- ZIO.service[IntegrationHealthcheckConfig]
        integrationChecks <- ZIO.service[Set[IntegrationCheck[Task]]]
        integrationCompletedChecks <-
          ZIO.foreachPar(integrationChecks ++ additionalIntegrationChecks) { integrationCheck =>
            integrationCheck
              .checkAvailability
              .timeout(config.timeout)
              .map(_.getOrElse(false))
              .map(integrationCheck.checkServiceName -> _)
          }
        _ <- ZIO
          .succeed(
            "\n" +
              integrationCompletedChecks
                .map { case (serviceName, status) =>
                  s"$serviceName: $status"
                }
                .mkString("\n")
          )
          .flatMap(statuses => ZIO.logDebug(s"Integration healthcheck statuses: $statuses"))
          .when(config.logStatuses)
        response <- ZIO.ifZIO(
          ZIO.succeed(
            integrationCompletedChecks
              .map { case (_, status) =>
                status
              }
              .forall(identity)
          )
        )(Ok(), ServiceUnavailable())
      } yield response
    ).provideSome[R](IntegrationHealthcheckConfig.live)
  }

}

object IntegrationHealthcheck {

  type Env = Set[IntegrationCheck[Task]]

}
