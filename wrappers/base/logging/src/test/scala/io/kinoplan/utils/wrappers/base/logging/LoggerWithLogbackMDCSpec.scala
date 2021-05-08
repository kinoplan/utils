package io.kinoplan.utils.wrappers.base.logging

import kit.data.TestKitConstants
import kit.mocks.LogbackMDCAdapter
import org.mockito.Mockito.verify
import org.scalatest.wordspec.AnyWordSpec
import org.slf4j.MDC

class LoggerWithLogbackMDCSpec extends AnyWordSpec with Loggable with TestKitConstants {
  "Logger#error with default LogbackMDCAdapter" should {
    "call the underlying logger's error method with a message" in {
      LogbackMDCAdapter.initialize()
      logger.error(message)(mapContext)
      underlying.error(message)(mapContext)
      verify(underlying).error(message)(mapContext)
      assert(
        MDC.getCopyOfContextMap == oldMDCMap || !MDC.getCopyOfContextMap.isEmpty
      )
    }
    "call the underlying logger's error method with a message and cause" in {
      LogbackMDCAdapter.initialize()
      logger.error(message, cause)(mapContext)
      underlying.error(message, cause)(mapContext)
      verify(underlying).error(message, cause)(mapContext)
      assert(
        MDC.getCopyOfContextMap == oldMDCMap || !MDC.getCopyOfContextMap.isEmpty
      )
    }
    "call the underlying logger's error method with a message and args" in {
      LogbackMDCAdapter.initialize()
      logger.error(message, arg1, arg2, arg3)(mapContext)
      underlying.error(message, arg1, arg2, arg3)(mapContext)
      verify(underlying).error(message, arg1, arg2, arg3)(mapContext)
      assert(
        MDC.getCopyOfContextMap == oldMDCMap || !MDC.getCopyOfContextMap.isEmpty
      )
    }
    "call the underlying logger's error method with a marker context and message" in {
      LogbackMDCAdapter.initialize()
      logger.error(dummyMarkerContext, message)(mapContext)
      underlying.error(dummyMarkerContext, message)(mapContext)
      verify(underlying).error(dummyMarkerContext, message)(mapContext)
      assert(
        MDC.getCopyOfContextMap == oldMDCMap || !MDC.getCopyOfContextMap.isEmpty
      )
    }
    "call the underlying logger's error method with a marker context and message and args" in {
      LogbackMDCAdapter.initialize()
      logger.error(dummyMarkerContext, arg1, arg2, arg3)(mapContext)
      underlying.error(dummyMarkerContext, arg1, arg2, arg3)(mapContext)
      verify(underlying).error(dummyMarkerContext, arg1, arg2, arg3)(mapContext)
      assert(
        MDC.getCopyOfContextMap == oldMDCMap || !MDC.getCopyOfContextMap.isEmpty
      )
    }
    "call the underlying logger's error method with a marker context and message and cause" in {
      LogbackMDCAdapter.initialize()
      logger.error(dummyMarkerContext, message, cause)(mapContext)
      underlying.error(dummyMarkerContext, message, cause)(mapContext)
      verify(underlying).error(dummyMarkerContext, message, cause)(mapContext)
      assert(
        MDC.getCopyOfContextMap == oldMDCMap || !MDC.getCopyOfContextMap.isEmpty
      )
    }
  }

  "Logger#warn with default LogbackMDCAdapter" should {
    "call the underlying logger's warn method with a message" in {
      LogbackMDCAdapter.initialize()
      logger.warn(message)(mapContext)
      underlying.warn(message)(mapContext)
      verify(underlying).warn(message)(mapContext)
      assert(
        MDC.getCopyOfContextMap == oldMDCMap || !MDC.getCopyOfContextMap.isEmpty
      )
    }
    "call the underlying logger's warn method with a message and cause" in {
      LogbackMDCAdapter.initialize()
      logger.warn(message, cause)(mapContext)
      underlying.warn(message, cause)(mapContext)
      verify(underlying).warn(message, cause)(mapContext)
      assert(
        MDC.getCopyOfContextMap == oldMDCMap || !MDC.getCopyOfContextMap.isEmpty
      )
    }
    "call the underlying logger's warn method with a message and args" in {
      LogbackMDCAdapter.initialize()
      logger.warn(message, arg1, arg2, arg3)(mapContext)
      underlying.warn(message, arg1, arg2, arg3)(mapContext)
      verify(underlying).warn(message, arg1, arg2, arg3)(mapContext)
      assert(
        MDC.getCopyOfContextMap == oldMDCMap || !MDC.getCopyOfContextMap.isEmpty
      )
    }
    "call the underlying logger's warn method with a marker context and message" in {
      LogbackMDCAdapter.initialize()
      logger.warn(dummyMarkerContext, message)(mapContext)
      underlying.warn(dummyMarkerContext, message)(mapContext)
      verify(underlying).warn(dummyMarkerContext, message)(mapContext)
      assert(
        MDC.getCopyOfContextMap == oldMDCMap || !MDC.getCopyOfContextMap.isEmpty
      )
    }
    "call the underlying logger's warn method with a marker context and message and args" in {
      LogbackMDCAdapter.initialize()
      logger.warn(dummyMarkerContext, arg1, arg2, arg3)(mapContext)
      underlying.warn(dummyMarkerContext, arg1, arg2, arg3)(mapContext)
      verify(underlying).warn(dummyMarkerContext, arg1, arg2, arg3)(mapContext)
      assert(
        MDC.getCopyOfContextMap == oldMDCMap || !MDC.getCopyOfContextMap.isEmpty
      )
    }
    "call the underlying logger's warn method with a marker context and message and cause" in {
      LogbackMDCAdapter.initialize()
      logger.warn(dummyMarkerContext, message, cause)(mapContext)
      underlying.warn(dummyMarkerContext, message, cause)(mapContext)
      verify(underlying).warn(dummyMarkerContext, message, cause)(mapContext)
      assert(
        MDC.getCopyOfContextMap == oldMDCMap || !MDC.getCopyOfContextMap.isEmpty
      )
    }
  }

  "Logger#info with default LogbackMDCAdapter" should {
    "call the underlying logger's info method with a message" in {
      LogbackMDCAdapter.initialize()
      logger.info(message)(mapContext)
      underlying.info(message)(mapContext)
      verify(underlying).info(message)(mapContext)
      assert(
        MDC.getCopyOfContextMap == oldMDCMap || !MDC.getCopyOfContextMap.isEmpty
      )
    }
    "call the underlying logger's info method with a message and cause" in {
      LogbackMDCAdapter.initialize()
      logger.info(message, cause)(mapContext)
      underlying.info(message, cause)(mapContext)
      verify(underlying).info(message, cause)(mapContext)
      assert(
        MDC.getCopyOfContextMap == oldMDCMap || !MDC.getCopyOfContextMap.isEmpty
      )
    }
    "call the underlying logger's info method with a message and args" in {
      LogbackMDCAdapter.initialize()
      logger.info(message, arg1, arg2, arg3)(mapContext)
      underlying.info(message, arg1, arg2, arg3)(mapContext)
      verify(underlying).info(message, arg1, arg2, arg3)(mapContext)
      assert(
        MDC.getCopyOfContextMap == oldMDCMap || !MDC.getCopyOfContextMap.isEmpty
      )
    }
    "call the underlying logger's info method with a marker context and message" in {
      LogbackMDCAdapter.initialize()
      logger.info(dummyMarkerContext, message)(mapContext)
      underlying.info(dummyMarkerContext, message)(mapContext)
      verify(underlying).info(dummyMarkerContext, message)(mapContext)
      assert(
        MDC.getCopyOfContextMap == oldMDCMap || !MDC.getCopyOfContextMap.isEmpty
      )
    }
    "call the underlying logger's info method with a marker context and message and args" in {
      LogbackMDCAdapter.initialize()
      logger.info(dummyMarkerContext, arg1, arg2, arg3)(mapContext)
      underlying.info(dummyMarkerContext, arg1, arg2, arg3)(mapContext)
      verify(underlying).info(dummyMarkerContext, arg1, arg2, arg3)(mapContext)
      assert(
        MDC.getCopyOfContextMap == oldMDCMap || !MDC.getCopyOfContextMap.isEmpty
      )
    }
    "call the underlying logger's info method with a marker context and message and cause" in {
      LogbackMDCAdapter.initialize()
      logger.info(dummyMarkerContext, message, cause)(mapContext)
      underlying.info(dummyMarkerContext, message, cause)(mapContext)
      verify(underlying).info(dummyMarkerContext, message, cause)(mapContext)
      assert(
        MDC.getCopyOfContextMap == oldMDCMap || !MDC.getCopyOfContextMap.isEmpty
      )
    }
  }

  "Logger#debug with default LogbackMDCAdapter" should {
    "call the underlying logger's debug method with a message" in {
      LogbackMDCAdapter.initialize()
      logger.debug(message)(mapContext)
      underlying.debug(message)(mapContext)
      verify(underlying).debug(message)(mapContext)
      assert(
        MDC.getCopyOfContextMap == oldMDCMap || !MDC.getCopyOfContextMap.isEmpty
      )
    }
    "call the underlying logger's debug method with a message and cause" in {
      LogbackMDCAdapter.initialize()
      logger.debug(message, cause)(mapContext)
      underlying.debug(message, cause)(mapContext)
      verify(underlying).debug(message, cause)(mapContext)
      assert(
        MDC.getCopyOfContextMap == oldMDCMap || !MDC.getCopyOfContextMap.isEmpty
      )
    }
    "call the underlying logger's debug method with a message and args" in {
      LogbackMDCAdapter.initialize()
      logger.debug(message, arg1, arg2, arg3)(mapContext)
      underlying.debug(message, arg1, arg2, arg3)(mapContext)
      verify(underlying).debug(message, arg1, arg2, arg3)(mapContext)
      assert(
        MDC.getCopyOfContextMap == oldMDCMap || !MDC.getCopyOfContextMap.isEmpty
      )
    }
    "call the underlying logger's debug method with a marker context and message" in {
      LogbackMDCAdapter.initialize()
      logger.debug(dummyMarkerContext, message)(mapContext)
      underlying.debug(dummyMarkerContext, message)(mapContext)
      verify(underlying).debug(dummyMarkerContext, message)(mapContext)
      assert(
        MDC.getCopyOfContextMap == oldMDCMap || !MDC.getCopyOfContextMap.isEmpty
      )
    }
    "call the underlying logger's debug method with a marker context and message and args" in {
      LogbackMDCAdapter.initialize()
      logger.debug(dummyMarkerContext, arg1, arg2, arg3)(mapContext)
      underlying.debug(dummyMarkerContext, arg1, arg2, arg3)(mapContext)
      verify(underlying).debug(dummyMarkerContext, arg1, arg2, arg3)(mapContext)
      assert(
        MDC.getCopyOfContextMap == oldMDCMap || !MDC.getCopyOfContextMap.isEmpty
      )
    }
    "call the underlying logger's debug method with a marker context and message and cause" in {
      LogbackMDCAdapter.initialize()
      logger.debug(dummyMarkerContext, message, cause)(mapContext)
      underlying.debug(dummyMarkerContext, message, cause)(mapContext)
      verify(underlying).debug(dummyMarkerContext, message, cause)(mapContext)
      assert(
        MDC.getCopyOfContextMap == oldMDCMap || !MDC.getCopyOfContextMap.isEmpty
      )
    }
  }

  "Logger#trace with default LogbackMDCAdapter" should {
    "call the underlying logger's trace method with a message" in {
      LogbackMDCAdapter.initialize()
      logger.trace(message)(mapContext)
      underlying.trace(message)(mapContext)
      verify(underlying).trace(message)(mapContext)
      assert(
        MDC.getCopyOfContextMap == oldMDCMap || !MDC.getCopyOfContextMap.isEmpty
      )
    }
    "call the underlying logger's trace method with a message and cause" in {
      LogbackMDCAdapter.initialize()
      logger.trace(message, cause)(mapContext)
      underlying.trace(message, cause)(mapContext)
      verify(underlying).trace(message, cause)(mapContext)
      assert(
        MDC.getCopyOfContextMap == oldMDCMap || !MDC.getCopyOfContextMap.isEmpty
      )
    }
    "call the underlying logger's trace method with a message and args" in {
      LogbackMDCAdapter.initialize()
      logger.trace(message, arg1, arg2, arg3)(mapContext)
      underlying.trace(message, arg1, arg2, arg3)(mapContext)
      verify(underlying).trace(message, arg1, arg2, arg3)(mapContext)
      assert(
        MDC.getCopyOfContextMap == oldMDCMap || !MDC.getCopyOfContextMap.isEmpty
      )
    }
    "call the underlying logger's trace method with a marker context and message" in {
      LogbackMDCAdapter.initialize()
      logger.trace(dummyMarkerContext, message)(mapContext)
      underlying.trace(dummyMarkerContext, message)(mapContext)
      verify(underlying).trace(dummyMarkerContext, message)(mapContext)
      assert(
        MDC.getCopyOfContextMap == oldMDCMap || !MDC.getCopyOfContextMap.isEmpty
      )
    }
    "call the underlying logger's trace method with a marker context and message and args" in {
      LogbackMDCAdapter.initialize()
      logger.trace(dummyMarkerContext, arg1, arg2, arg3)(mapContext)
      underlying.trace(dummyMarkerContext, arg1, arg2, arg3)(mapContext)
      verify(underlying).trace(dummyMarkerContext, arg1, arg2, arg3)(mapContext)
      assert(
        MDC.getCopyOfContextMap == oldMDCMap || !MDC.getCopyOfContextMap.isEmpty
      )
    }
    "call the underlying logger's trace method with a marker context and message and cause" in {
      LogbackMDCAdapter.initialize()
      logger.trace(dummyMarkerContext, message, cause)(mapContext)
      underlying.trace(dummyMarkerContext, message, cause)(mapContext)
      verify(underlying).trace(dummyMarkerContext, message, cause)(mapContext)
      assert(
        MDC.getCopyOfContextMap == oldMDCMap || !MDC.getCopyOfContextMap.isEmpty
      )
    }
  }
}
