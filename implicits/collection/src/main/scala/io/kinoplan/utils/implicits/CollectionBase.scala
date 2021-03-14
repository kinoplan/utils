package io.kinoplan.utils.implicits

private[implicits] trait CollectionBase[A] {

  protected def zipWith[B >: A](f: A => B): Map[B, A]

  protected def zipBoth[B, I](k: A => B, v: A => I): Map[B, I]

  @inline
  def zipWithDefaultValue[B >: A](f: A => B)(default: A): Map[B, A] = zipWith(f)
    .withDefaultValue(default)

  @inline
  def zipBothWithDefaultValue[B, I](k: A => B, v: A => I)(default: I): Map[B, I] = zipBoth(k, v)
    .withDefaultValue(default)

}
