package io.kinoplan.utils.wrappers.base.logging

import kit.data.TestKitConstants
import org.mockito.Mockito.verify
import org.scalatest.wordspec.AnyWordSpec

class LoggerSpec extends AnyWordSpec with Loggable with TestKitConstants {
  "Logger#error" should {
    "call the underlying logger's error method with a message" in {
      logger.error(message)
      underlying.error(message)
      verify(underlying).error(message)
    }
    "call the underlying logger's error method with a message and cause" in {
      logger.error(message, cause)
      underlying.error(message, cause)
      verify(underlying).error(message, cause)
    }
    "call the underlying logger's error method with a message and args" in {
      logger.error(message, arg1, arg2, arg3)
      underlying.error(message, arg1, arg2, arg3)
      verify(underlying).error(message, arg1, arg2, arg3)
    }
    "call the underlying logger's error method with a marker context and message" in {
      logger.error(dummyMarkerContext, message)
      underlying.error(dummyMarkerContext, message)
      verify(underlying).error(dummyMarkerContext, message)
    }
    "call the underlying logger's error method with a marker context and message and args" in {
      logger.error(dummyMarkerContext, arg1, arg2, arg3)
      underlying.error(dummyMarkerContext, arg1, arg2, arg3)
      verify(underlying).error(dummyMarkerContext, arg1, arg2, arg3)
    }
    "call the underlying logger's error method with a marker context and message and cause" in {
      logger.error(dummyMarkerToMarkerContext, message, cause)
      underlying.error(dummyMarkerToMarkerContext, message, cause)
      verify(underlying).error(dummyMarkerToMarkerContext, message, cause)
    }
  }

  "Logger#warn" should {
    "call the underlying logger's warn method with a message" in {
      logger.warn(message)
      underlying.warn(message)
      verify(underlying).warn(message)
    }
    "call the underlying logger's warn method with a message and cause" in {
      logger.warn(message, cause)
      underlying.warn(message, cause)
      verify(underlying).warn(message, cause)
    }
    "call the underlying logger's warn method with a message and args" in {
      logger.warn(message, arg1, arg2, arg3)
      underlying.warn(message, arg1, arg2, arg3)
      verify(underlying).warn(message, arg1, arg2, arg3)
    }
    "call the underlying logger's warn method with a marker context and message" in {
      underlying.warn(dummyMarkerContext, message)
      verify(underlying).warn(dummyMarkerContext, message)
    }
    "call the underlying logger's warn method with a marker context and message and args" in {
      logger.warn(dummyMarkerContext, arg1, arg2, arg3)
      underlying.warn(dummyMarkerContext, arg1, arg2, arg3)
      verify(underlying).warn(dummyMarkerContext, arg1, arg2, arg3)
    }
    "call the underlying logger's warn method with a marker context and message and cause" in {
      logger.warn(dummyMarkerContext, message, cause)
      underlying.warn(dummyMarkerContext, message, cause)
      verify(underlying).warn(dummyMarkerContext, message, cause)
    }
  }

  "Logger#info" should {
    "call the underlying logger's info method with a message" in {
      logger.info(message)
      underlying.info(message)
      verify(underlying).info(message)
    }
    "call the underlying logger's info method with a message and cause" in {
      logger.info(message, cause)
      underlying.info(message, cause)
      verify(underlying).info(message, cause)
    }
    "call the underlying logger's info method with a message and args" in {
      logger.info(message, arg1, arg2, arg3)
      underlying.info(message, arg1, arg2, arg3)
      verify(underlying).info(message, arg1, arg2, arg3)
    }
    "call the underlying logger's info method with a marker context and message" in {
      logger.info(dummyMarkerContext, message)
      underlying.info(dummyMarkerContext, message)
      verify(underlying).info(dummyMarkerContext, message)
    }
    "call the underlying logger's info method with a marker context and message and args" in {
      logger.info(dummyMarkerContext, arg1, arg2, arg3)
      underlying.info(dummyMarkerContext, arg1, arg2, arg3)
      verify(underlying).info(dummyMarkerContext, arg1, arg2, arg3)
    }
    "call the underlying logger's info method with a marker context and message and cause" in {
      logger.info(dummyMarkerContext, message, cause)
      underlying.info(dummyMarkerContext, message, cause)
      verify(underlying).info(dummyMarkerContext, message, cause)
    }
  }

  "Logger#debug" should {
    "call the underlying logger's debug method with a message" in {
      logger.debug(message)
      underlying.debug(message)
      verify(underlying).debug(message)
    }
    "call the underlying logger's debug method with a message and cause" in {
      logger.debug(message, cause)
      underlying.debug(message, cause)
      verify(underlying).debug(message, cause)
    }
    "call the underlying logger's debug method with a message and args" in {
      logger.debug(message, arg1, arg2, arg3)
      underlying.debug(message, arg1, arg2, arg3)
      verify(underlying).debug(message, arg1, arg2, arg3)
    }
    "call the underlying logger's debug method with a marker context and message" in {
      logger.debug(dummyMarkerContext, message)
      underlying.debug(dummyMarkerContext, message)
      verify(underlying).debug(dummyMarkerContext, message)
    }
    "call the underlying logger's debug method with a marker context and message and args" in {
      logger.debug(dummyMarkerContext, arg1, arg2, arg3)
      underlying.debug(dummyMarkerContext, arg1, arg2, arg3)
      verify(underlying).debug(dummyMarkerContext, arg1, arg2, arg3)
    }
    "call the underlying logger's debug method with a marker context and message and cause" in {
      logger.debug(dummyMarkerContext, message, cause)
      underlying.debug(dummyMarkerContext, message, cause)
      verify(underlying).debug(dummyMarkerContext, message, cause)
    }
  }

  "Logger#trace" should {
    "call the underlying logger's trace method with a message" in {
      logger.trace(message)
      underlying.trace(message)
      verify(underlying).trace(message)
    }
    "call the underlying logger's trace method with a message and cause" in {
      logger.trace(message, cause)
      underlying.trace(message, cause)
      verify(underlying).trace(message, cause)
    }
    "call the underlying logger's trace method with a message and args" in {
      logger.trace(message, arg1, arg2, arg3)
      underlying.trace(message, arg1, arg2, arg3)
      verify(underlying).trace(message, arg1, arg2, arg3)
    }
    "call the underlying logger's trace method with a marker context and message" in {
      logger.trace(dummyMarkerContext, message)
      underlying.trace(dummyMarkerContext, message)
      verify(underlying).trace(dummyMarkerContext, message)
    }
    "call the underlying logger's trace method with a marker context and message and args" in {
      logger.trace(dummyMarkerContext, arg1, arg2, arg3)
      underlying.trace(dummyMarkerContext, arg1, arg2, arg3)
      verify(underlying).trace(dummyMarkerContext, arg1, arg2, arg3)
    }
    "call the underlying logger's trace method with a marker context and message and cause" in {
      logger.trace(dummyMarkerContext, message, cause)
      underlying.trace(dummyMarkerContext, message, cause)
      verify(underlying).trace(dummyMarkerContext, message, cause)
    }
  }
}
