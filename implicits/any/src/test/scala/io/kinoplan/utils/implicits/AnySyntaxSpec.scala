package io.kinoplan.utils.implicits

import org.scalatest.wordspec.AnyWordSpec

import io.kinoplan.utils.implicits.AnySyntax.syntaxAnyOps

class AnySyntaxSpec extends AnyWordSpec {
  val intAny: Any = 123456
  val doubleAny: Any = 123456.7d
  val floatAny: Any = 123456.7f
  val longAny: Any = 123456L
  val stringAny: Any = "123456"
  val invalidStringAny: Any = "test"
  val booleanAny: Any = true

  "toIntOption" should {
    "return correct value Int" in assert(intAny.toIntOption.contains(123456))
    "return correct value Double" in assert(doubleAny.toIntOption.contains(123456))
    "return correct value Float" in assert(floatAny.toIntOption.contains(123456))
    "return correct value Long" in assert(longAny.toIntOption.contains(123456))
    "return correct value String" in assert(stringAny.toIntOption.contains(123456))
    "return correct value invalid String" in assert(invalidStringAny.toIntOption.isEmpty)
    "return correct value Boolean" in assert(booleanAny.toIntOption.isEmpty)
  }

}
