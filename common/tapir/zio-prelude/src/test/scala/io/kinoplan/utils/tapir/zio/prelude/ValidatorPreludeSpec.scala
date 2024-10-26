package io.kinoplan.utils.tapir.zio.prelude

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import sttp.tapir.{ValidationError, Validator}
import zio.prelude.{NonEmptyList, NonEmptyMap, NonEmptySet, NonEmptySortedMap, NonEmptySortedSet}
import zio.prelude.coherent.DeriveEqualNonEmptyForEach.derive

class ValidatorPreludeSpec extends AnyFlatSpec with Matchers {

  it should "success with non-empty list - NonEmptyList" in {
    val value: NonEmptyList[String] = NonEmptyList("A", "B", "C")
    val result: Seq[ValidationError[_]] = ValidatorPrelude
      .nonEmptyForEach[NonEmptyList, String]
      .apply(value)
    result shouldBe Nil
  }

  it should "success with non-empty list - List" in {
    val value: List[String] = List("A", "B", "C")
    val result: Seq[ValidationError[_]] = ValidatorPrelude.nonEmptyForEach[List, String].apply(value)
    result shouldBe Nil
  }

  it should "fail with empty list - List" in {
    val value: List[String] = Nil
    val result: Seq[ValidationError[_]] = ValidatorPrelude.nonEmptyForEach[List, String].apply(value)
    result shouldBe List(ValidationError(Validator.minSize[String, List](1), List(), List()))
  }

  it should "success with non-empty set - NonEmptySet" in {
    val value: NonEmptySet[String] = NonEmptySet("A", "B", "C")
    val result: Seq[ValidationError[_]] = ValidatorPrelude.nonEmptySet[String].apply(value)
    result shouldBe Nil
  }

  it should "success with non-empty sorted set - NonEmptySortedSet" in {
    val value: NonEmptySortedSet[String] = NonEmptySortedSet("A", "B", "C")
    val result: Seq[ValidationError[_]] = ValidatorPrelude.nonEmptySortedSet[String].apply(value)
    result shouldBe Nil
  }

  it should "success with non-empty map - NonEmptyMap" in {
    val value: NonEmptyMap[String, Int] = NonEmptyMap(("A", 1), ("B", 2), ("C", 3))
    val result: Seq[ValidationError[_]] = ValidatorPrelude.nonEmptyMap[String, Int].apply(value)
    result shouldBe Nil
  }

  it should "success with non-empty sorted map - NonEmptySortedMap" in {
    val value: NonEmptySortedMap[String, Int] = NonEmptySortedMap(("A", 1), ("B", 2), ("C", 3))
    val result: Seq[ValidationError[_]] = ValidatorPrelude
      .nonEmptySortedMap[String, Int]
      .apply(value)
    result shouldBe Nil
  }

}
