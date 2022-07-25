package io.kinoplan.utils.scala.logging.context

class MapContext private (private[logging] val underlyingMap: java.util.Map[String, String]) {

  @inline
  def isEmpty: Boolean = underlyingMap.isEmpty

  @inline
  def get(key: String): Option[String] = Option(underlyingMap.get(key))

  def put(pairs: (String, Any)*): MapContext = {
    pairs.collect {
      case (k, Some(v))                         => k -> v.toString
      case (k, v) if !v.isInstanceOf[Option[_]] => k -> v.toString
    }.foreach { case (k, v) => underlyingMap.put(k, v) }
    this
  }

  @inline
  def remove(key: String): Option[String] = Option(underlyingMap.remove(key))

  @inline
  def clear(): Unit = underlyingMap.clear()

}

object MapContext {

  def apply(): MapContext = new MapContext(new java.util.HashMap[String, String])

  def apply(elems: (String, String)*): MapContext = apply().put(elems: _*)

  def apply(map: Map[String, Any]): MapContext = apply().put(map.toIndexedSeq: _*)

  implicit val empty: MapContext = MapContext()
}
