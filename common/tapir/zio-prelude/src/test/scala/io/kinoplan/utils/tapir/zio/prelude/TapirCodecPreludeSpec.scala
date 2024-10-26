package io.kinoplan.utils.tapir.zio.prelude

import scala.collection.immutable.SortedSet

import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.Checkers
import sttp.tapir.{Codec, CodecFormat, DecodeResult, Schema, Validator}
import sttp.tapir.SchemaType.{SArray, SString}
import zio.NonEmptyChunk
import zio.prelude.{NonEmptyList, NonEmptyMap, NonEmptySet, NonEmptySortedMap, NonEmptySortedSet}

class TapirCodecPreludeSpec extends AnyFlatSpec with Matchers with Checkers with TapirCodecPrelude {
  case class Test(value: String)

  implicit val schemaForTest: Schema[Test] = Schema
    .derived[Test]
    .validate(Validator.minLength(3).contramap(_.value))

  implicit val schemaForNemTest: Schema[NonEmptyMap[Int, Test]] = schemaForNem[Int, Test](_.toString)

  implicit val schemaForSortedNemTest: Schema[NonEmptySortedMap[Int, Test]] =
    schemaForSortedNem[Int, Test](_.toString)

  it should "find schema for prelude collections" in {
    implicitly[Schema[NonEmptyList[String]]].schemaType shouldBe
      SArray[NonEmptyList[String], String](Schema(SString()))(_.toList)
    implicitly[Schema[NonEmptyList[String]]].isOptional shouldBe false

    implicitly[Schema[NonEmptyChunk[String]]].schemaType shouldBe
      SArray[NonEmptyChunk[String], String](Schema(SString()))(_.toList)
    implicitly[Schema[NonEmptyChunk[String]]].isOptional shouldBe false

    implicitly[Schema[NonEmptySet[String]]].schemaType shouldBe
      SArray[NonEmptySet[String], String](Schema(SString()))(_.toSet)
    implicitly[Schema[NonEmptySet[String]]].isOptional shouldBe false

    implicitly[Schema[NonEmptySortedSet[String]]].schemaType shouldBe
      SArray[NonEmptySortedSet[String], String](Schema(SString()))(_.toSet)
    implicitly[Schema[NonEmptySortedSet[String]]].isOptional shouldBe false

    implicitly[Schema[NonEmptyMap[String, Int]]].schemaType shouldBe
      Schema.schemaForMap[Int].schemaType
    implicitly[Schema[NonEmptyMap[String, Int]]].isOptional shouldBe false

    implicitly[Schema[NonEmptySortedMap[String, Int]]].schemaType shouldBe
      Schema.schemaForMap[String, Int](identity).schemaType
    implicitly[Schema[NonEmptySortedMap[String, Int]]].isOptional shouldBe false

    implicitly[Schema[NonEmptyMap[Int, Test]]].schemaType shouldBe
      Schema.schemaForMap[Int, Test](_.toString).schemaType
    implicitly[Schema[NonEmptyMap[Int, Test]]].isOptional shouldBe false

    implicitly[Schema[NonEmptySortedMap[Int, Test]]].schemaType shouldBe
      Schema.schemaForMap[Int, Test](_.toString).schemaType
    implicitly[Schema[NonEmptySortedMap[Int, Test]]].isOptional shouldBe false
  }

  it should "find proper validator for prelude collections" in {

    def expectedValidator[C[X] <: Iterable[X]] = schemaForTest
      .asIterable[C]
      .validate(Validator.minSize(1))

    def expectedMapValidator[K, V: Schema](keyToString: K => String) = Schema
      .schemaForMap[K, V](keyToString)
      .validate(Validator.minSize(1))

    implicitly[Schema[NonEmptyList[Test]]].showValidators shouldBe
      expectedValidator[List].showValidators
    implicitly[Schema[NonEmptyChunk[Test]]].showValidators shouldBe
      expectedValidator[List].showValidators
    implicitly[Schema[NonEmptySet[Test]]].showValidators shouldBe
      expectedValidator[Set].showValidators
    implicitly[Schema[NonEmptySortedSet[Test]]].showValidators shouldBe
      expectedValidator[SortedSet].showValidators
    implicitly[Schema[NonEmptyMap[String, Test]]].showValidators shouldBe
      expectedMapValidator[String, Test](identity).showValidators
    implicitly[Schema[NonEmptySortedMap[String, Test]]].showValidators shouldBe
      expectedMapValidator[String, Test](identity).showValidators
    implicitly[Schema[NonEmptyMap[Int, Test]]].showValidators shouldBe
      expectedMapValidator[Int, Test](_.toString).showValidators
    implicitly[Schema[NonEmptySortedMap[Int, Test]]].showValidators shouldBe
      expectedMapValidator[Int, Test](_.toString).showValidators
  }

  implicit def arbitraryNonEmptyList[T: Arbitrary]: Arbitrary[NonEmptyList[T]] = Arbitrary(
    Gen
      .nonEmptyListOf(implicitly[Arbitrary[T]].arbitrary)
      .map(NonEmptyList.fromIterableOption(_).get)
  )

  implicit def arbitraryNonEmptyChunk[T: Arbitrary]: Arbitrary[NonEmptyChunk[T]] = Arbitrary(
    Gen
      .nonEmptyListOf(implicitly[Arbitrary[T]].arbitrary)
      .map(NonEmptyChunk.fromIterableOption(_).get)
  )

  implicit def arbitraryNonEmptySet[T: Arbitrary]: Arbitrary[NonEmptySet[T]] = Arbitrary(
    Gen
      .nonEmptyBuildableOf[Set[T], T](implicitly[Arbitrary[T]].arbitrary)
      .map(NonEmptySet.fromIterableOption(_).get)
  )

  implicit def arbitraryNonEmptySortedSet[T: Arbitrary: Ordering]: Arbitrary[NonEmptySortedSet[T]] =
    Arbitrary(
      Gen
        .nonEmptyBuildableOf[SortedSet[T], T](implicitly[Arbitrary[T]].arbitrary)
        .map(NonEmptySortedSet.fromIterableOption(_).get)
    )

  "Provided PlainText coder for non empty list" should "correctly serialize a non empty list" in {
    val codecForNel = implicitly[Codec[List[String], NonEmptyList[String], CodecFormat.TextPlain]]
    val rawCodec = implicitly[Codec[List[String], List[String], CodecFormat.TextPlain]]
    check((a: NonEmptyList[String]) => codecForNel.encode(a) == rawCodec.encode(a.toList))
  }

  it should "correctly deserialize everything it serialize" in {
    val codecForNel = implicitly[Codec[List[String], NonEmptyList[String], CodecFormat.TextPlain]]
    check((a: NonEmptyList[String]) =>
      codecForNel.decode(codecForNel.encode(a)) == DecodeResult.Value(a)
    )
  }

  it should "fail on empty list" in {
    val codecForNel = implicitly[Codec[List[String], NonEmptyList[String], CodecFormat.TextPlain]]
    codecForNel.decode(Nil) shouldBe DecodeResult.Missing
  }

  it should "have the proper schema for list" in {
    val codecForNel = implicitly[Codec[List[String], NonEmptyList[String], CodecFormat.TextPlain]]
    codecForNel.schema.copy(validator = Validator.pass) shouldBe
      implicitly[Schema[NonEmptyList[String]]].copy(validator = Validator.pass)
    codecForNel.schema.validator.show shouldBe
      implicitly[Schema[NonEmptyList[String]]].validator.show
  }

  it should "have the proper validator for list" in {
    val codecForNel = implicitly[Codec[List[String], NonEmptyList[String], CodecFormat.TextPlain]]
    codecForNel.schema.showValidators shouldBe
      implicitly[Schema[NonEmptyList[String]]].showValidators
  }

  "Provided PlainText codec for non empty chunk" should "correctly serialize a non empty chunk" in {
    val codecForNec = implicitly[Codec[List[String], NonEmptyChunk[String], CodecFormat.TextPlain]]
    val rawCodec = implicitly[Codec[List[String], List[String], CodecFormat.TextPlain]]
    check((a: NonEmptyChunk[String]) => codecForNec.encode(a) == rawCodec.encode(a.toList))
  }

  it should "correctly deserialize everything it serialize for chunk" in {
    val codecForNec = implicitly[Codec[List[String], NonEmptyChunk[String], CodecFormat.TextPlain]]
    check((a: NonEmptyChunk[String]) =>
      codecForNec.decode(codecForNec.encode(a)) == DecodeResult.Value(a)
    )
  }

  it should "fail on empty chunk" in {
    val codecForNec = implicitly[Codec[List[String], NonEmptyChunk[String], CodecFormat.TextPlain]]
    codecForNec.decode(Nil) shouldBe DecodeResult.Missing
  }

  it should "have the proper schema for chunk" in {
    val codecForNec = implicitly[Codec[List[String], NonEmptyChunk[String], CodecFormat.TextPlain]]
    codecForNec.schema.copy(validator = Validator.pass) shouldBe
      implicitly[Schema[NonEmptyChunk[String]]].copy(validator = Validator.pass)
    codecForNec.schema.validator.show shouldBe
      implicitly[Schema[NonEmptyChunk[String]]].validator.show
  }

  it should "have the proper validator for chunk" in {
    val codecForNec = implicitly[Codec[List[String], NonEmptyChunk[String], CodecFormat.TextPlain]]
    codecForNec.schema.showValidators shouldBe
      implicitly[Schema[NonEmptyChunk[String]]].showValidators
  }

  "Provided PlainText codec for non empty set" should "correctly serialize a non empty set" in {
    val codecForNes = implicitly[Codec[List[String], NonEmptySet[String], CodecFormat.TextPlain]]
    val rawCodec = implicitly[Codec[List[String], Set[String], CodecFormat.TextPlain]]
    check((a: NonEmptySet[String]) => codecForNes.encode(a) == rawCodec.encode(a.toSet))
  }

  it should "correctly deserialize everything it serialize for set" in {
    val codecForNes = implicitly[Codec[List[String], NonEmptySet[String], CodecFormat.TextPlain]]
    check((a: NonEmptySet[String]) =>
      codecForNes.decode(codecForNes.encode(a)) == DecodeResult.Value(a)
    )
  }

  it should "fail on empty set" in {
    val codecForNes = implicitly[Codec[List[String], NonEmptySet[String], CodecFormat.TextPlain]]
    codecForNes.decode(Nil) shouldBe DecodeResult.Missing
  }

  it should "have the proper schema for set" in {
    val codecForNes = implicitly[Codec[List[String], NonEmptySet[String], CodecFormat.TextPlain]]
    codecForNes.schema.copy(validator = Validator.pass) shouldBe
      implicitly[Schema[NonEmptySet[String]]].copy(validator = Validator.pass)
    codecForNes.schema.validator.show shouldBe
      implicitly[Schema[NonEmptySet[String]]].validator.show
  }

  it should "have the proper validator for set" in {
    val codecForNes = implicitly[Codec[List[String], NonEmptySet[String], CodecFormat.TextPlain]]
    codecForNes.schema.showValidators shouldBe
      implicitly[Schema[NonEmptySet[String]]].showValidators
  }

  "Provided PlainText codec for non empty sorted set" should
    "correctly serialize a non empty sorted set" in {
      val codecForSortedNes =
        implicitly[Codec[List[String], NonEmptySortedSet[String], CodecFormat.TextPlain]]
      val rawCodec = implicitly[Codec[List[String], Set[String], CodecFormat.TextPlain]]
      check((a: NonEmptySortedSet[String]) => codecForSortedNes.encode(a) == rawCodec.encode(a.toSet))
    }

  it should "correctly deserialize everything it serialize for sorted set" in {
    val codecForSortedNes =
      implicitly[Codec[List[String], NonEmptySortedSet[String], CodecFormat.TextPlain]]
    check((a: NonEmptySortedSet[String]) =>
      codecForSortedNes.decode(codecForSortedNes.encode(a)) == DecodeResult.Value(a)
    )
  }

  it should "fail on empty sorted set" in {
    val codecForSortedNes =
      implicitly[Codec[List[String], NonEmptySortedSet[String], CodecFormat.TextPlain]]
    codecForSortedNes.decode(Nil) shouldBe DecodeResult.Missing
  }

  it should "have the proper schema for sorted set" in {
    val codecForSortedNes =
      implicitly[Codec[List[String], NonEmptySortedSet[String], CodecFormat.TextPlain]]
    codecForSortedNes.schema.copy(validator = Validator.pass) shouldBe
      implicitly[Schema[NonEmptySortedSet[String]]].copy(validator = Validator.pass)
    codecForSortedNes.schema.validator.show shouldBe
      implicitly[Schema[NonEmptySortedSet[String]]].validator.show
  }

  it should "have the proper validator for sorted set" in {
    val codecForSortedNes =
      implicitly[Codec[List[String], NonEmptySortedSet[String], CodecFormat.TextPlain]]
    codecForSortedNes.schema.showValidators shouldBe
      implicitly[Schema[NonEmptySortedSet[String]]].showValidators
  }

}
