package io.kinoplan.utils.implicits

import scala.collection.BuildFrom
import scala.collection.generic.IsIterable

final class CollectionOps[Repr, I <: IsIterable[Repr]](private val value: Repr, it: I) {

  @inline
  def intersectBy[B >: it.A, That](f: it.A => B)(container: Iterable[B])(implicit
    bf: BuildFrom[Repr, it.A, That]
  ): That = {
    val thisOps = it(value)

    bf.fromSpecific(value)(
      thisOps.view.filter { a =>
        val b = f(a)

        container.exists(_ == b)
      }
    )
  }

}

trait CollectionSyntax {

  implicit def syntaxCollectionOps[Repr](value: Repr)(implicit
    it: IsIterable[Repr]
  ): CollectionOps[Repr, it.type] = new CollectionOps(value, it)

}

object CollectionSyntax extends CollectionSyntax
