package io.kinoplan.utils.chimney.zio.prelude

import io.scalaland.chimney.dsl.{PartialTransformerOps, TransformerOps}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import zio.NonEmptyChunk
import zio.prelude.{NonEmptyList, NonEmptyMap, NonEmptySet, NonEmptySortedMap, NonEmptySortedSet}

class ChimneyImplicitsPreludeSpec extends AnyFlatSpec with Matchers with ChimneyImplicitsPrelude {
  case class Test(key: Int, value: String)
  case class TestDTO(key: Int, value: String)

  val test: Test = Test(1, "test")
  val testDTO: TestDTO = TestDTO(1, "test")
  val tupledTest: (Int, Test) = (test.key, test)
  val tupledTestDTO: (Int, TestDTO) = (testDTO.key, testDTO)

  implicit val orderingTest: Ordering[Test] = Ordering.by[Test, Int](_.key)
  implicit val orderingTestDTO: Ordering[TestDTO] = Ordering.by[TestDTO, Int](_.key)

  it should "DSL always allow transformation between NonEmptyLists" in {
    NonEmptyList.single(test).transformInto[NonEmptyList[TestDTO]] shouldBe
      NonEmptyList.single(testDTO)
  }

  it should "DSL handle transformation to and from NonEmptyList" in {
    List(test).transformIntoPartial[NonEmptyList[TestDTO]].asOption shouldBe
      Some(NonEmptyList.single(testDTO))
    List.empty[Test].transformIntoPartial[NonEmptyList[TestDTO]].asOption shouldBe None

    NonEmptyList.single(test).transformInto[List[TestDTO]] shouldBe List(testDTO)
  }

  it should "DSL always allow transformation between NonEmptyChunks" in {
    NonEmptyChunk.single(test).transformInto[NonEmptyChunk[TestDTO]] shouldBe
      NonEmptyChunk.single(testDTO)
  }

  it should "DSL handle transformation to and from NonEmptyChunk" in {
    List(test).transformIntoPartial[NonEmptyChunk[TestDTO]].asOption shouldBe
      Some(NonEmptyChunk.single(testDTO))
    List.empty[Test].transformIntoPartial[NonEmptyChunk[TestDTO]].asOption shouldBe None

    NonEmptyChunk.single(test).transformInto[List[TestDTO]] shouldBe List(testDTO)
  }

  it should "DSL always allow transformation between NonEmptySets" in {
    NonEmptySet.single(test).transformInto[NonEmptySet[TestDTO]] shouldBe NonEmptySet.single(testDTO)
  }

  it should "DSL handle transformation to and from NonEmptySet" in {
    Set(test).transformIntoPartial[NonEmptySet[TestDTO]].asOption shouldBe
      Some(NonEmptySet.single(testDTO))
    Set.empty[Test].transformIntoPartial[NonEmptySet[TestDTO]].asOption shouldBe None

    NonEmptySet.single(test).transformInto[Set[TestDTO]] shouldBe Set(testDTO)
  }

  it should "DSL always allow transformation between NonEmptySortedSets" in {
    NonEmptySortedSet.single(test).transformInto[NonEmptySortedSet[TestDTO]] shouldBe
      NonEmptySortedSet.single(testDTO)
  }

  it should "DSL handle transformation to and from NonEmptySortedSet" in {
    Set(test).transformIntoPartial[NonEmptySortedSet[TestDTO]].asOption shouldBe
      Some(NonEmptySortedSet.single(testDTO))
    Set.empty[Test].transformIntoPartial[NonEmptySortedSet[TestDTO]].asOption shouldBe None

    NonEmptySortedSet.single(test).transformInto[Set[TestDTO]] shouldBe Set(testDTO)
  }

  it should "DSL always allow transformation between NonEmptyMaps" in {
    NonEmptyMap.single(tupledTest).transformInto[NonEmptyMap[Int, TestDTO]] shouldBe
      NonEmptyMap.single(tupledTestDTO)
  }

  it should "DSL handle transformation to and from NonEmptyMap" in {
    Map(tupledTest).transformIntoPartial[NonEmptyMap[Int, TestDTO]].asOption shouldBe
      Some(NonEmptyMap.single(tupledTestDTO))
    Map.empty[Int, Test].transformIntoPartial[NonEmptyMap[Int, TestDTO]].asOption shouldBe None

    NonEmptyMap.single(tupledTest).transformInto[Map[Int, TestDTO]] shouldBe Map(tupledTestDTO)
  }

  it should "DSL always allow transformation between NonEmptySortedMaps" in {
    NonEmptySortedMap.single(tupledTest).transformInto[NonEmptySortedMap[Int, TestDTO]] shouldBe
      NonEmptySortedMap.single(tupledTestDTO)
  }

  it should "DSL handle transformation to and from NonEmptySortedMap" in {
    Map(tupledTest).transformIntoPartial[NonEmptySortedMap[Int, TestDTO]].asOption shouldBe
      Some(NonEmptySortedMap.single(tupledTestDTO))
    Map.empty[Int, Test].transformIntoPartial[NonEmptySortedMap[Int, TestDTO]].asOption shouldBe
      None

    NonEmptySortedMap.single(tupledTest).transformInto[Map[Int, TestDTO]] shouldBe Map(tupledTestDTO)
  }

}
