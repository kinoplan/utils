package io.circe

import scala.collection.{Iterable, mutable}

object CirceAccessors {

  def nonEmptySeqDecoder[A, C[_] <: Iterable[_], S](
    builder: mutable.Builder[A, C[A]],
    f: (A, C[A]) => S
  )(implicit
    decoder: Decoder[A]
  ): Decoder[S] = new NonEmptySeqDecoder[A, C, S](decoder) {
    final protected def createBuilder(): mutable.Builder[A, C[A]] = builder
    final protected val create: (A, C[A]) => S = (h, t) => f(h, t)
  }

}
