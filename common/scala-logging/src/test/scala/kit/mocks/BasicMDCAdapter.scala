package kit.mocks

import java.util.Collections

import org.slf4j.MDC

object BasicMDCAdapter {

  def initialize(): Unit = {
    val field = classOf[MDC].getDeclaredField("MDC_ADAPTER")
    field.setAccessible(true)
    field.set(null, new org.slf4j.helpers.BasicMDCAdapter)
    field.setAccessible(false)
    MDC.getMDCAdapter.setContextMap(Collections.singletonMap("key1", "value1"))
  }

}
