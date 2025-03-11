package kit.mocks

import org.slf4j.Marker

object DummyMarker extends Marker {
  override def add(childMarker: Marker): Unit = ()
  override def contains(childName: String): Boolean = false
  override def contains(child: Marker): Boolean = false
  override def getName: String = "DummyMarker"
  override def hasChildren: Boolean = false
  override def hasReferences: Boolean = false

  override def iterator(): java.util.Iterator[Marker] = new java.util.Iterator[Marker] {
    override def hasNext: Boolean = false
    override def next(): Marker = throw new NoSuchElementException
    override def remove(): Unit = throw new NoSuchElementException
  }

  override def remove(child: Marker): Boolean = false
}
