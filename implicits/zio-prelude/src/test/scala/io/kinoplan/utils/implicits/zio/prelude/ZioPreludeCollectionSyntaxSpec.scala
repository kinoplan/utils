package io.kinoplan.utils.implicits.zio.prelude

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import zio.NonEmptyChunk
import zio.prelude.{NonEmptyList, NonEmptyMap, NonEmptySet, NonEmptySortedMap, NonEmptySortedSet}

import io.kinoplan.utils.implicits.zio.prelude.ZioPreludeCollectionSyntax.syntaxZioPreludeCollectionOps

class ZioPreludeCollectionSyntaxSpec extends AnyFlatSpec with Matchers {

  case class Test(key: Int, value: String)

  implicit val orderingTest: Ordering[Test] = Ordering.by[Test, Int](_.key)

  val test: Iterable[Test] = Iterable(Test(1, "test1"), Test(2, "test2"), Test(3, "test3"))
  val testTupled: Iterable[(Int, Test)] = test.map(t => (t.key, t))

  it should "success convert toNel" in {
    test.toNel shouldBe NonEmptyList.fromIterableOption(test)
  }

  it should "success convert toNec" in {
    test.toNec shouldBe NonEmptyChunk.fromIterableOption(test)
  }

  it should "success convert toNes" in {
    test.toNes shouldBe NonEmptySet.fromIterableOption(test)
  }

  it should "success convert toSortedNes" in {
    test.toSortedNes shouldBe NonEmptySortedSet.fromIterableOption(test)
  }

  it should "success convert toNem" in {
    testTupled.toNem shouldBe NonEmptyMap.fromIterableOption(testTupled)
  }

  it should "success convert toSortedNem" in {
    testTupled.toSortedNem shouldBe NonEmptySortedMap.fromIterableOption(testTupled)
  }

}
