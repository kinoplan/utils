package io.kinoplan.utils.reactivemongo.bson.zio.prelude

import reactivemongo.api.bson.{
  BSONArray,
  BSONDocument,
  BSONDocumentHandler,
  BSONElement,
  Macros,
  document
}
import zio.NonEmptyChunk
import zio.prelude.{NonEmptyList, NonEmptyMap, NonEmptySet, NonEmptySortedMap, NonEmptySortedSet}

case class TestData(
  nes: NonEmptySet[Int],
  ness: NonEmptySortedSet[String],
  nel: NonEmptyList[Int],
  nec: NonEmptyChunk[Int],
  nem: NonEmptyMap[String, Int],
  nesm: NonEmptySortedMap[String, Int]
)

trait TestDataBson extends BsonZioPreludeHandlers {
  implicit val handler: BSONDocumentHandler[TestData] = Macros.handler
}

object TestData extends TestDataBson {

  val data: TestData = TestData(
    nes = NonEmptySet(1, 1, 2, 3),
    ness = NonEmptySortedSet("a", "b", "c", "d", "a"),
    nel = NonEmptyList(1, 1, 2, 3, 4, 5, 6),
    nec = NonEmptyChunk(1, 2, 2, 3),
    nem = NonEmptyMap("a" -> 3, "c" -> 1, "b" -> 2),
    nesm = NonEmptySortedMap("a" -> 3, "c" -> 1, "b" -> 2)
  )

  val emptyBson: BSONArray = BSONArray.empty
  val badBson: BSONArray = BSONArray(1, true, "3")

  val emptyDoc: BSONDocument = BSONDocument.empty

  val badDoc: BSONDocument =
    BSONDocument(BSONElement("a", 1), BSONElement("b", true), BSONElement("c", "3"))

  val nesBson: BSONArray = BSONArray(3, 2, 1)
  val nessBson: BSONArray = BSONArray("d", "c", "b", "a")
  val nelBson: BSONArray = BSONArray(6, 5, 4, 3, 2, 1, 1)
  val necBson: BSONArray = BSONArray(3, 2, 2, 1)

  val nemBson: BSONDocument =
    BSONDocument(BSONElement("a", 3), BSONElement("b", 2), BSONElement("c", 1))

  val nesmBson: BSONDocument =
    BSONDocument(BSONElement("c", 1), BSONElement("b", 2), BSONElement("a", 3))

  val bson: BSONDocument = document(
    "nes" -> nesBson,
    "ness" -> nessBson,
    "nel" -> nelBson,
    "nec" -> necBson,
    "nem" -> nemBson,
    "nesm" -> nesmBson
  )

  val emptyNesBson: BSONDocument = bson ++ document("nes" -> emptyBson)
  val badNesBson: BSONDocument = bson ++ document("nes" -> badBson)

  val emptyNessBson: BSONDocument = bson ++ document("ness" -> badBson)
  val badNessBson: BSONDocument = bson ++ document("ness" -> emptyBson)

  val emptyNelBson: BSONDocument = bson ++ document("nel" -> emptyBson)
  val badNelBson: BSONDocument = bson ++ document("nel" -> badBson)

  val emptyNecBson: BSONDocument = bson ++ document("nec" -> emptyBson)
  val badNecBson: BSONDocument = bson ++ document("nec" -> badBson)

  val emptyNemBson: BSONDocument = bson ++ document("nem" -> emptyDoc)
  val badNemBson: BSONDocument = bson ++ document("nem" -> badDoc)

  val emptyNesmBson: BSONDocument = bson ++ document("nesm" -> emptyDoc)
  val badNesmBson: BSONDocument = bson ++ document("nesm" -> badDoc)

}
