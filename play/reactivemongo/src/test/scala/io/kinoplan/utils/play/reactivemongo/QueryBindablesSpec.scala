package io.kinoplan.utils.play.reactivemongo

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import reactivemongo.api.bson._

class QueryBindablesSpec extends AnyWordSpec with Matchers with QueryBindables {

  val keyParam = "testParam"

  val bindValue: String => Map[String, Seq[String]] = value => Map(keyParam -> Seq(value))

  "BSONBooleanQueryBindable" should {
    "be valid bind" in {
      BSONBooleanQueryBindable.bind(keyParam, bindValue("true")) mustBe
        Some(Right(BSONBoolean(true)))
      BSONBooleanQueryBindable.bind(keyParam, bindValue("false")) mustBe
        Some(Right(BSONBoolean(false)))
    }

    "be valid unbind" in {
      BSONBooleanQueryBindable.unbind(keyParam, BSONBoolean(true)) mustBe s"$keyParam=true"
      BSONBooleanQueryBindable.unbind(keyParam, BSONBoolean(false)) mustBe s"$keyParam=false"
    }

    "be invalid bind" in {
      BSONBooleanQueryBindable.bind(keyParam, bindValue("test")) mustBe
        Some(Left("Cannot parse parameter testParam as BSONBoolean: For input string: \"test\""))
    }
  }

  "BSONDateTimeQueryBindable" should {
    val value = 1660864186L

    "be valid bind" in {
      BSONDateTimeQueryBindable.bind(keyParam, bindValue(value.toString)) mustBe
        Some(Right(BSONDateTime(value)))
    }

    "be valid unbind" in {
      BSONDateTimeQueryBindable.unbind(keyParam, BSONDateTime(value)) mustBe s"$keyParam=$value"
    }

    "be invalid bind" in {
      BSONDateTimeQueryBindable.bind(keyParam, bindValue("test")) mustBe
        Some(Left("Cannot parse parameter testParam as BSONDateTime: For input string: \"test\""))
    }
  }

  "BSONDoubleQueryBindable" should {
    val value = 2.56d

    "be valid bind" in {
      BSONDoubleQueryBindable.bind(keyParam, bindValue(value.toString)) mustBe
        Some(Right(BSONDouble(value)))
    }

    "be valid unbind" in {
      BSONDoubleQueryBindable.unbind(keyParam, BSONDouble(value)) mustBe s"$keyParam=$value"
    }

    "be invalid bind" in {
      BSONDoubleQueryBindable.bind(keyParam, bindValue("test")) mustBe
        Some(Left("Cannot parse parameter testParam as BSONDouble: For input string: \"test\""))
    }
  }

  "BSONLongQueryBindable" should {
    val value = 1660864186L

    "be valid bind" in {
      BSONLongQueryBindable.bind(keyParam, bindValue(value.toString)) mustBe
        Some(Right(BSONLong(value)))
    }

    "be valid unbind" in {
      BSONLongQueryBindable.unbind(keyParam, BSONLong(value)) mustBe s"$keyParam=$value"
    }

    "be invalid bind" in {
      BSONLongQueryBindable.bind(keyParam, bindValue("test")) mustBe
        Some(Left("Cannot parse parameter testParam as BSONLong: For input string: \"test\""))
    }
  }

  "BSONStringQueryBindable" should {
    val value = "some text"

    "be valid bind" in {
      BSONStringQueryBindable.bind(keyParam, bindValue(value)) mustBe Some(Right(BSONString(value)))
    }

    "be valid unbind" in {
      BSONStringQueryBindable.unbind(keyParam, BSONString(value)) mustBe s"$keyParam=$value"
    }
  }

  "BSONSymbolQueryBindable" should {
    val value = "some text"

    "be valid bind" in {
      BSONSymbolQueryBindable.bind(keyParam, bindValue(value)) mustBe Some(Right(BSONSymbol(value)))
    }

    "be valid unbind" in {
      BSONSymbolQueryBindable.unbind(keyParam, BSONSymbol(value)) mustBe s"$keyParam=$value"
    }
  }

  "BSONTimestampQueryBindable" should {
    val value = 1660864186L

    "be valid bind" in {
      BSONTimestampQueryBindable.bind(keyParam, bindValue(value.toString)) mustBe
        Some(Right(BSONTimestamp(value)))
    }

    "be valid unbind" in {
      BSONTimestampQueryBindable.unbind(keyParam, BSONTimestamp(value)) mustBe s"$keyParam=$value"
    }

    "be invalid bind" in {
      BSONTimestampQueryBindable.bind(keyParam, bindValue("test")) mustBe
        Some(Left("Cannot parse parameter testParam as BSONTimestamp: For input string: \"test\""))
    }
  }

  "BSONObjectIDQueryBindable" should {
    val value = "62fcf67708000026005a6fe9"

    "be valid bind" in {
      BSONObjectIDQueryBindable.bind(keyParam, bindValue(value)) mustBe
        Some(Right(BSONObjectID.parse(value).get))
    }

    "be valid unbind" in {
      BSONObjectIDQueryBindable.unbind(keyParam, BSONObjectID.parse(value).get) mustBe
        s"$keyParam=$value"
    }

    "be invalid bind" in {
      BSONObjectIDQueryBindable.bind(keyParam, bindValue("test")) mustBe
        Some(Left("Cannot parse parameter testParam as BSONObjectID: Wrong ObjectId (length != 24): 'test'"))
    }
  }

}
