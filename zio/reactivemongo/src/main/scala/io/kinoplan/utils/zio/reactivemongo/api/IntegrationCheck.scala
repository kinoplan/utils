package io.kinoplan.utils.zio.reactivemongo.api

trait IntegrationCheck[F[_]] {
  def checkAvailability: F[Boolean]
}
