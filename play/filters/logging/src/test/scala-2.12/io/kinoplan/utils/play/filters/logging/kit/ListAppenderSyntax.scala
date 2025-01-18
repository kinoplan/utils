package io.kinoplan.utils.play.filters.logging.kit

import scala.collection.JavaConverters._
import scala.collection.immutable.Seq

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender

class ListAppenderOps(private val value: ListAppender[ILoggingEvent]) extends AnyVal {

  def getLogMessages: Seq[String] = collection
    .immutable
    .Seq(value.list.asScala.map(_.getMessage): _*)

  def getLogMdcProperties: Map[String, String] = value
    .list
    .asScala
    .flatMap(_.getMDCPropertyMap.asScala)
    .toMap

}

trait ListAppenderSyntax {

  implicit final def syntaxListAppenderOps(value: ListAppender[ILoggingEvent]): ListAppenderOps =
    new ListAppenderOps(value)

}

object ListAppenderSyntax extends ListAppenderSyntax
