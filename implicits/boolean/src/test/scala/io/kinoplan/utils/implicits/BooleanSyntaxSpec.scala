package io.kinoplan.utils.implicits

import org.scalatest.wordspec.AnyWordSpec

import io.kinoplan.utils.implicits.BooleanSyntax.syntaxBooleanOps

class BooleanSyntaxSpec extends AnyWordSpec {

  "BooleanSyntax#toOption" should {
    "return correct value" in {
      assert(true.toOption === Some(true))
      assert(false.toOption === None)
    }
  }
}
