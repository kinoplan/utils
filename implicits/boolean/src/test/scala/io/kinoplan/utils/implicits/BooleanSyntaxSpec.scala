package io.kinoplan.utils.implicits

import org.scalatest.wordspec.AnyWordSpec

import io.kinoplan.utils.implicits.BooleanSyntax.syntaxBooleanOps

class BooleanSyntaxSpec extends AnyWordSpec {
  val textLeft = "textLeft"
  val textRight = "textRight"

  "BooleanSyntax#toOption" should {
    "return correct value" in {
      assert(false.toOption.isEmpty)
      assert(true.toOption.contains(true))
    }
  }

  "BooleanSyntax#toRight" should {
    "return correct value" in {
      assert(false.toRight(textLeft) === Left[String, Unit](textLeft))
      assert(true.toRight(textLeft) === Right[String, Unit](()))
    }
  }

  "BooleanSyntax#toEither" should {
    "return correct value" in {
      assert(
        false.toEither(textLeft, textRight) === Left[String, String](textLeft)
      )
      assert(
        true.toEither(textLeft, textRight) === Right[String, String](textRight)
      )
    }
  }

  "BooleanSyntax#toInt" should {
    "return correct value" in {
      assert(false.toInt === 0)
      assert(true.toInt === 1)
    }
  }

  "BooleanSyntax#fold" should {
    "return correct value" in {
      assert(false.fold(-1)(10) === -1)
      assert(true.fold(-1)(10) === 10)
    }
  }

  "BooleanSyntax#foldList" should {
    "return correct value" in {
      assert(false.foldList(List(1)) === List(1))
      assert(true.foldList(List(1)) === List())
    }
  }

  "BooleanSyntax#when" should {
    "return correct value" in {
      assert(false.when(10).isEmpty)
      assert(true.when(10).contains(10))
    }
  }

  "BooleanSyntax#unless" should {
    "return correct value" in {
      assert(false.unless(10).contains(10))
      assert(true.unless(10).isEmpty)
    }
  }
}
