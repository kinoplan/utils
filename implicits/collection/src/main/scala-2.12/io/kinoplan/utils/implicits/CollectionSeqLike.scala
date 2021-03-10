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

    operation match {
      case CollectionOperation.Intersection => i
          .foreach(o => if (container.exists(_ == f(o))) builder += o)
      case CollectionOperation.Difference => i
          .foreach(o => if (!container.exists(_ == f(o))) builder += o)
    }

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
    val i = container.iterator

    operation match {
      case CollectionOperation.Intersection => i
          .foreach(o => if (container.exists(f(_) == f(o))) builder += o)
      case CollectionOperation.Difference =>
        var temp = Set[A]()

        i.foreach(o => if (!value.exists(f(_) == f(o))) temp += o)

        builder ++= value
        builder ++= temp
    }

    builder.result()
  }

}
