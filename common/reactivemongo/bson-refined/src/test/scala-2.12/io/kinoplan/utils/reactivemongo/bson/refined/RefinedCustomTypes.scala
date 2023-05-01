package io.kinoplan.utils.reactivemongo.bson.refined

import eu.timepit.refined._
import eu.timepit.refined.api.{Refined, RefinedTypeOps}
import eu.timepit.refined.numeric._

object RefinedCustomTypes {
  type Percent = Int Refined Interval.Closed[W.`0`.T, W.`100`.T]

  object Percent extends RefinedTypeOps[Percent, Int]
}
