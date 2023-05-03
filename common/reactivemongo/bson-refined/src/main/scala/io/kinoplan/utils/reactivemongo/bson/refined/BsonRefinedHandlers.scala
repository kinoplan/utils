package io.kinoplan.utils.reactivemongo.bson.refined

import eu.timepit.refined.api.{Refined, Validate}
import eu.timepit.refined.refineV
import reactivemongo.api.bson.{BSONHandler, BSONReader, BSONWriter}

trait BsonRefinedHandlers {

  implicit def bsonRefinedHandler[T, P](implicit
    validate: Validate[T, P],
    r: BSONReader[T],
    w: BSONWriter[T]
  ): BSONHandler[Refined[T, P]] = BSONHandler.from(
    bson => r.readTry(bson).flatMap(refineV[P](_).left.map(new IllegalArgumentException(_)).toTry),
    t => w.writeTry(t.value)
  )

}

object BsonRefinedHandlers extends BsonRefinedHandlers
