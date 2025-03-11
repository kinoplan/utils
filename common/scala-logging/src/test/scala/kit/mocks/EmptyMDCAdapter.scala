package kit.mocks

import org.slf4j.MDC

object EmptyMDCAdapter {

  def initialize(): Unit = {
    val field = classOf[MDC].getDeclaredField("MDC_ADAPTER")
    field.setAccessible(true)
    field.set(null, new org.slf4j.helpers.BasicMDCAdapter)
    field.setAccessible(false)
  }

}
