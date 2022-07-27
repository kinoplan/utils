package io.kinoplan.utils.logback.config

import scala.beans.BeanProperty

import ch.qos.logback.core.PropertyDefinerBase
import com.typesafe.config.ConfigFactory

class ConfigPropertyDefiner extends PropertyDefinerBase {

  @BeanProperty
  var path: String = _

  override def getPropertyValue: String = ConfigFactory.load().getString(path)
}
