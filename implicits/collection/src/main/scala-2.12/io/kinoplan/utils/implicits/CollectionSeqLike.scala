package io.kinoplan.utils.implicits

import scala.collection.{GenIterable, IterableLike}
import scala.collection.generic.CanBuildFrom

sealed private[implicits] trait CollectionOperation

private[implicits] object CollectionOperation {
  case object Intersection extends CollectionOperation
  case object Difference extends CollectionOperation
}

abstract private[implicits] class CollectionSeqLike[A, Repr](
  private val value: IterableLike[A, Repr]
) {

  def computesOperationBy[B, That](
    f: A => B,
    container: GenIterable[B],
    operation: CollectionOperation
  )(implicit
    cbf: CanBuildFrom[Repr, A, That]
  ): That = {
    val builder = cbf(value.repr)
    val i = value.iterator

    builder ++=
      (operation match {
        case CollectionOperation.Intersection => i.filter(o => container.exists(_ == f(o)))
        case CollectionOperation.Difference   => i.filterNot(o => container.exists(_ == f(o)))
      })

    builder.result()
  }

  def computesOperationByMerge[B, That](
    f: A => B,
    container: GenIterable[A],
    operation: CollectionOperation
  )(implicit
    cbf: CanBuildFrom[Repr, A, That]
  ): That = {
    val builder = cbf(value.repr)
    val i = value.iterator

    builder ++=
      (operation match {
        case CollectionOperation.Intersection => i.filter(o => container.exists(f(_) == f(o)))
        case CollectionOperation.Difference => i ++
            container.filterNot(o => value.exists(f(_) == f(o)))
      })

    builder.result()
  }

}
