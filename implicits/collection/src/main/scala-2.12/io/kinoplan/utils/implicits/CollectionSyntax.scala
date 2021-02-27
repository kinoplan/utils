package io.kinoplan.utils.implicits

import scala.collection.{GenIterable, IterableLike}
import scala.collection.generic.CanBuildFrom

trait CollectionSyntax {

  implicit def syntaxCollectionOps[A, Repr](value: IterableLike[A, Repr]): CollectionOps[A, Repr] =
    new CollectionOps(value)

  class CollectionOps[A, Repr](private val value: IterableLike[A, Repr]) {

    @inline
    def intersectBy[I, That](f: A => I)(container: GenIterable[I])(implicit cbf: CanBuildFrom[Repr, A, That]): That = {
      val builder = cbf(value.repr)
      val i = value.iterator

      while (i.hasNext) {
        val o = i.next
        val b = f(o)

        if (container.exists(_ == b)) builder += o
      }

      builder.result()
    }
  }
}

object CollectionSyntax extends CollectionSyntax
