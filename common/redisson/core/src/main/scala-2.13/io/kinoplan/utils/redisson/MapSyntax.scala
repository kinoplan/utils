package io.kinoplan.utils.redisson

final private[redisson] class MapOps[K, V](private val value: Map[K, V]) extends AnyVal {

  @inline
  def crossMapValues[W](f: V => W): Map[K, W] = value.view.mapValues(f).toMap

}

private[redisson] trait MapSyntax {
  implicit final def syntaxMapOps[K, V](value: Map[K, V]): MapOps[K, V] = new MapOps[K, V](value)
}

private[redisson] object MapSyntax extends MapSyntax
