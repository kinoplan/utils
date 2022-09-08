package io.kinoplan.utils.reactivemongo.base

import reactivemongo.api.bson.{MacroConfiguration, MacroOptions}
import reactivemongo.api.bson.FieldNaming.SnakeCase
import reactivemongo.api.bson.MacroConfiguration.Aux

trait BsonDefaultSnakeCase {
  implicit val config: Aux[MacroOptions.ReadDefaultValues] = MacroConfiguration(SnakeCase)
}

object BsonDefaultSnakeCase extends BsonDefaultSnakeCase
