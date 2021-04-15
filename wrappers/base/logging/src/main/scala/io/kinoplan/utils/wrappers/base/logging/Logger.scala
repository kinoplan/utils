package io.kinoplan.utils.wrappers.base.logging

import ch.qos.logback.classic.util.LogbackMDCAdapter
import com.typesafe.scalalogging.{Logger => ScalaLogger}
import org.slf4j.MDC

import io.kinoplan.utils.wrappers.base.logging.context.{MapContext, MarkerContext}

class Logger(name: String) {
  protected val log: ScalaLogger = ScalaLogger(name)

  // Error

  @inline
  def error(message: String)(implicit
    mapContext: MapContext
  ): Unit = error(MarkerContext.NoMarker, message)

  @inline
  def error(message: String, cause: Throwable)(implicit
    mapContext: MapContext
  ): Unit = error(MarkerContext.NoMarker, message, cause)

  @inline
  def error(message: String, args: Any*)(implicit
    mapContext: MapContext
  ): Unit = error(MarkerContext.NoMarker, message, args)

  @inline
  def error(markerContext: MarkerContext, message: String)(implicit
    mapContext: MapContext
  ): Unit = withMDC {
    markerContext.marker match {
      case Some(marker) => log.error(marker, message)
      case _            => log.error(message)
    }
  }

  @inline
  def error(markerContext: MarkerContext, message: String, args: Any*)(implicit
    mapContext: MapContext
  ): Unit = withMDC {
    markerContext.marker match {
      case Some(marker) => log.error(marker, message, args)
      case _            => log.error(message, args)
    }
  }

  @inline
  def error(markerContext: MarkerContext, message: String, cause: Throwable)(implicit
    mapContext: MapContext
  ): Unit = withMDC {
    markerContext.marker match {
      case Some(marker) => log.error(marker, message, cause)
      case _            => log.error(message, cause)
    }
  }

  // Warn

  @inline
  def warn(message: String)(implicit
    mapContext: MapContext
  ): Unit = warn(MarkerContext.NoMarker, message)

  @inline
  def warn(message: String, cause: Throwable)(implicit
    mapContext: MapContext
  ): Unit = warn(MarkerContext.NoMarker, message, cause)

  @inline
  def warn(message: String, args: Any*)(implicit
    mapContext: MapContext
  ): Unit = warn(MarkerContext.NoMarker, message, args)

  @inline
  def warn(markerContext: MarkerContext, message: String)(implicit
    mapContext: MapContext
  ): Unit = withMDC {
    markerContext.marker match {
      case Some(marker) => log.warn(marker, message)
      case _            => log.warn(message)
    }
  }

  def warn(markerContext: MarkerContext, message: String, args: Any*)(implicit
    mapContext: MapContext
  ): Unit = withMDC {
    markerContext.marker match {
      case Some(marker) => log.warn(marker, message, args)
      case _            => log.warn(message, args)
    }
  }

  @inline
  def warn(markerContext: MarkerContext, message: String, cause: Throwable)(implicit
    mapContext: MapContext
  ): Unit = withMDC {
    markerContext.marker match {
      case Some(marker) => log.warn(marker, message, cause)
      case _            => log.warn(message, cause)
    }
  }

  // Info

  @inline
  def info(message: String)(implicit
    mapContext: MapContext
  ): Unit = info(MarkerContext.NoMarker, message)

  @inline
  def info(message: String, cause: Throwable)(implicit
    mapContext: MapContext
  ): Unit = info(MarkerContext.NoMarker, message, cause)

  @inline
  def info(message: String, args: Any*)(implicit
    mapContext: MapContext
  ): Unit = info(MarkerContext.NoMarker, message, args)

  @inline
  def info(markerContext: MarkerContext, message: String)(implicit
    mapContext: MapContext
  ): Unit = withMDC {
    markerContext.marker match {
      case Some(marker) => log.info(marker, message)
      case _            => log.info(message)
    }
  }

  @inline
  def info(markerContext: MarkerContext, message: String, args: Any*)(implicit
    mapContext: MapContext
  ): Unit = withMDC {
    markerContext.marker match {
      case Some(marker) => log.info(marker, message, args)
      case _            => log.info(message, args)
    }
  }

  @inline
  def info(markerContext: MarkerContext, message: String, cause: Throwable)(implicit
    mapContext: MapContext
  ): Unit = withMDC {
    markerContext.marker match {
      case Some(marker) => log.info(marker, message, cause)
      case _            => log.info(message, cause)
    }
  }

  // Debug

  @inline
  def debug(message: String)(implicit
    mapContext: MapContext
  ): Unit = debug(MarkerContext.NoMarker, message)

  @inline
  def debug(message: String, cause: Throwable)(implicit
    mapContext: MapContext
  ): Unit = debug(MarkerContext.NoMarker, message, cause)

  @inline
  def debug(message: String, args: Any*)(implicit
    mapContext: MapContext
  ): Unit = debug(MarkerContext.NoMarker, message, args)

  @inline
  def debug(markerContext: MarkerContext, message: String)(implicit
    mapContext: MapContext
  ): Unit = withMDC {
    markerContext.marker match {
      case Some(marker) => log.debug(marker, message)
      case _            => log.debug(message)
    }
  }

  @inline
  def debug(markerContext: MarkerContext, message: String, args: Any*)(implicit
    mapContext: MapContext
  ): Unit = withMDC {
    markerContext.marker match {
      case Some(marker) => log.debug(marker, message, args)
      case _            => log.debug(message, args)
    }
  }

  @inline
  def debug(markerContext: MarkerContext, message: String, cause: Throwable)(implicit
    mapContext: MapContext
  ): Unit = withMDC {
    markerContext.marker match {
      case Some(marker) => log.debug(marker, message, cause)
      case _            => log.debug(message, cause)
    }
  }

  // Trace

  @inline
  def trace(message: String)(implicit
    mapContext: MapContext
  ): Unit = trace(MarkerContext.NoMarker, message)

  @inline
  def trace(message: String, cause: Throwable)(implicit
    mapContext: MapContext
  ): Unit = trace(MarkerContext.NoMarker, message, cause)

  @inline
  def trace(message: String, args: Any*)(implicit
    mapContext: MapContext
  ): Unit = trace(MarkerContext.NoMarker, message, args)

  @inline
  def trace(markerContext: MarkerContext, message: String)(implicit
    mapContext: MapContext
  ): Unit = withMDC {
    markerContext.marker match {
      case Some(marker) => log.trace(marker, message)
      case _            => log.trace(message)
    }
  }

  @inline
  def trace(markerContext: MarkerContext, message: String, args: Any*)(implicit
    mapContext: MapContext
  ): Unit = withMDC {
    markerContext.marker match {
      case Some(marker) => log.trace(marker, message, args)
      case _            => log.trace(message, args)
    }
  }

  @inline
  def trace(markerContext: MarkerContext, message: String, cause: Throwable)(implicit
    mapContext: MapContext
  ): Unit = withMDC {
    markerContext.marker match {
      case Some(marker) => log.trace(marker, message, cause)
      case _            => log.trace(message, cause)
    }
  }

  private def withMDC(logAction: => Unit)(implicit
    mapContext: MapContext
  ): Unit =
    if (mapContext.isEmpty) logAction
    else {
      val oldMDCMap = MDC.getMDCAdapter match {
        case logbackAdapter: LogbackMDCAdapter => logbackAdapter.getPropertyMap
        case _                                 => MDC.getMDCAdapter.getCopyOfContextMap
      }

      MDC.setContextMap(mapContext.underlyingMap)

      try logAction
      finally if (oldMDCMap == null) MDC.clear() else MDC.setContextMap(oldMDCMap)
    }

}

object Logger {
  def apply(name: String): Logger = new Logger(name)
}
