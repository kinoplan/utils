package io.kinoplan.utils.wrappers.base.logging.context

import org.slf4j.Marker

trait MarkerContext {
  def marker: Option[Marker]
}

object MarkerContext extends LowPriorityMarkerContextImplicits {
  def apply(marker: Marker): MarkerContext = new DefaultMarkerContext(marker)
}

trait LowPriorityMarkerContextImplicits {
  val NoMarker: MarkerContext = MarkerContext(null)

  implicit def markerToMarkerContext(marker: Marker): MarkerContext = MarkerContext(marker)
}

class DefaultMarkerContext(someMarker: Marker) extends MarkerContext {
  def marker: Option[Marker] = Option(someMarker)
}
