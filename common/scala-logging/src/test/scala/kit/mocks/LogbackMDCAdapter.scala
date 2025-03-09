package kit.mocks

import org.slf4j.MDC

object LogbackMDCAdapter {

  def initialize(): Unit = {
    val field = classOf[MDC].getDeclaredField("MDC_ADAPTER")
    field.setAccessible(true)
    field.set(null, new ch.qos.logback.classic.util.LogbackMDCAdapter)
    field.setAccessible(false)
  }

}
