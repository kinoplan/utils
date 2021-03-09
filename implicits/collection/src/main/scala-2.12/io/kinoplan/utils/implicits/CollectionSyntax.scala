package io.kinoplan.utils.implicits

import scala.collection.{GenIterable, IterableLike}
import scala.collection.generic.CanBuildFrom

final class CollectionOps[A, Repr](private val value: IterableLike[A, Repr]) {

  @inline
  def intersectBy[I, That](f: A => I)(container: GenIterable[I])(implicit
    cbf: CanBuildFrom[Repr, A, That]
  ): That = {
    val builder = cbf(value.repr)
    val i = value.iterator

    i.foreach { o =>
      val b = f(o)

      if (container.exists(_ == b)) builder += o
    }

    builder.result()
  }

}

trait CollectionSyntax {

  implicit def syntaxCollectionOps[A, Repr](value: IterableLike[A, Repr]): CollectionOps[A, Repr] =
    new CollectionOps(value)

}

object CollectionSyntax extends CollectionSyntax
