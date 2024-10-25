package io.circe

import scala.collection.Map

abstract class NonEmptyMapDecoder[K, V, M[K, V] <: Map[K, V]](
  keyDecoder: KeyDecoder[K],
  decoder: Decoder[V]
) extends MapDecoder[K, V, M](keyDecoder, decoder)
