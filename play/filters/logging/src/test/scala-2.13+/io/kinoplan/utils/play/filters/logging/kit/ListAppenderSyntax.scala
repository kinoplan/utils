package io.kinoplan.utils.play.filters.logging.kit

import scala.jdk.CollectionConverters._

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender

class ListAppenderOps(private val value: ListAppender[ILoggingEvent]) extends AnyVal {

  def getLogMessages: Seq[String] = value.list.asScala.toSeq.map(_.getMessage)

  def getLogMdcProperties: Map[String, String] = value
    .list
    .asScala
    .toSeq
    .flatMap(_.getMDCPropertyMap.asScala)
    .toMap

}

trait ListAppenderSyntax {

  implicit final def syntaxListAppenderOps(value: ListAppender[ILoggingEvent]): ListAppenderOps =
    new ListAppenderOps(value)

}

object ListAppenderSyntax extends ListAppenderSyntax
