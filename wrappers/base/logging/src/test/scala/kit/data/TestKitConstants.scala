package kit.data

import ch.qos.logback.classic.util.LogbackMDCAdapter
import kit.mocks.DummyMarker
import org.scalatestplus.mockito.MockitoSugar.mock
import org.slf4j.MDC

import io.kinoplan.utils.wrappers.base.logging.Logger
import io.kinoplan.utils.wrappers.base.logging.context.{MapContext, MarkerContext}

trait TestKitConstants {
  val message = "message"
  val cause = new RuntimeException("cause")
  val zero = 0
  val arg1 = "arg1"
  val arg2 = 1
  val arg3 = true
  val dummyMarkerContext: MarkerContext = MarkerContext(DummyMarker)
  val dummyMarkerToMarkerContext: MarkerContext = MarkerContext.markerToMarkerContext(DummyMarker)
  val underlying: Logger = mock[Logger]

  val mapContext: MapContext = MapContext(Map("arg1" -> arg1, "arg2" -> arg2, "arg3" -> Some(arg3)))

  def oldMDCMap: java.util.Map[String, String] = MDC.getMDCAdapter match {
    case logbackAdapter: LogbackMDCAdapter => logbackAdapter.getPropertyMap
    case _                                 => MDC.getMDCAdapter.getCopyOfContextMap
  }

}
