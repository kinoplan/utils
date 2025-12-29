package io.kinoplan.utils.reactivemongo.bson.refined

import eu.timepit.refined.api.{Refined, RefinedTypeOps}
import eu.timepit.refined.numeric._

object RefinedCustomTypes {
  type Percent = Int Refined Interval.Closed[0, 100]

  object Percent extends RefinedTypeOps[Percent, Int]
}
