package io.kinoplan.utils

trait IntegrationCheck[F[_]] {
  val checkServiceName: String
  def checkAvailability: F[Boolean]
}
