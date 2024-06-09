package io.kinoplan.utils.implicits.identity

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import io.kinoplan.utils.implicits.identity.IdentitySyntax._

class IdentitySyntaxSpec extends AnyFlatSpec with Matchers {

  case class IdentityTest(boolValue: Boolean, optionValue: Option[Int], listValue: List[Int]) {
    def setBoolValue(value: Boolean): IdentityTest = copy(boolValue = value)

    def enableBoolValue: IdentityTest = copy(boolValue = true)

    def disableBoolValue: IdentityTest = copy(boolValue = false)

    def setOptionValue(value: Int): IdentityTest = copy(optionValue = Some(value))

    def setListValue(value: List[Int]): IdentityTest = copy(listValue = value)
  }

  object IdentityTest {
    val empty: IdentityTest = IdentityTest(boolValue = false, None, Nil)
    val nonEmpty: IdentityTest = IdentityTest(boolValue = true, Some(1), List(1))
  }

  "applyTo" should "apply given function to the entity and return result" in {
    IdentityTest.empty.applyTo(identity) shouldBe IdentityTest.empty
    IdentityTest.empty.applyTo(_.enableBoolValue) shouldBe IdentityTest.empty.enableBoolValue
    IdentityTest.empty.applyTo(_.boolValue) shouldBe false
  }

  "applyOption" should "apply function if Some, return entity otherwise" in {
    val entity1 = IdentityTest
      .empty
      .applyOption(_.optionValue)((builder, value) => builder.setOptionValue(value + 1))
    val entity2 = IdentityTest
      .nonEmpty
      .applyOption(_.optionValue)((builder, value) => builder.setOptionValue(value + 1))
    val entity3 = IdentityTest
      .nonEmpty
      .applyOption(Some(3))((builder, value) => builder.setOptionValue(value))

    entity1 shouldBe IdentityTest.empty
    entity2 shouldBe IdentityTest.nonEmpty.setOptionValue(2)
    entity3 shouldBe IdentityTest.nonEmpty.setOptionValue(3)
  }

  "applyOptionFold" should "apply function for Some and another for None" in {
    val entity1 = IdentityTest
      .empty
      .applyOptionFold(_.optionValue)(
        (builder, value) => builder.setOptionValue(value + 1),
        _.enableBoolValue
      )
    val entity2 = IdentityTest
      .nonEmpty
      .applyOptionFold(_.optionValue)(
        (builder, value) => builder.setOptionValue(value + 1),
        _.enableBoolValue
      )
    val entity3 = IdentityTest
      .nonEmpty
      .applyOptionFold(Some(3))((builder, value) => builder.setOptionValue(value), _.enableBoolValue)

    entity1 shouldBe IdentityTest.empty.enableBoolValue
    entity2 shouldBe IdentityTest.nonEmpty.setOptionValue(2)
    entity3 shouldBe IdentityTest.nonEmpty.setOptionValue(3)
  }

  "applyNonEmpty" should "apply function for non-empty collection" in {
    val entity1 = IdentityTest
      .empty
      .applyNonEmpty(_.listValue)((builder, value) => builder.setListValue(value :+ 2))
    val entity2 = IdentityTest
      .nonEmpty
      .applyNonEmpty(_.listValue)((builder, value) => builder.setListValue(value :+ 2))
    val entity3 = IdentityTest
      .nonEmpty
      .applyNonEmpty(List(3))((builder, value) => builder.setListValue(value))

    entity1 shouldBe IdentityTest.empty
    entity2 shouldBe IdentityTest.nonEmpty.setListValue(List(1, 2))
    entity3 shouldBe IdentityTest.nonEmpty.setListValue(List(3))
  }

  "applyWhen" should "apply function only if condition is true" in {
    val entity1 = IdentityTest.empty.applyWhen(_.boolValue)(_.enableBoolValue)
    val entity2 = IdentityTest.nonEmpty.applyWhen(_.boolValue)(_.disableBoolValue)
    val entity3 = IdentityTest.nonEmpty.applyWhen(IdentityTest.empty.boolValue)(_.disableBoolValue)

    entity1 shouldBe IdentityTest.empty
    entity2 shouldBe IdentityTest.nonEmpty.disableBoolValue
    entity3 shouldBe IdentityTest.nonEmpty
  }

  "applyUnless" should "apply function only if condition is false" in {
    val entity1 = IdentityTest.empty.applyUnless(_.boolValue)(_.enableBoolValue)
    val entity2 = IdentityTest.nonEmpty.applyUnless(_.boolValue)(_.disableBoolValue)
    val entity3 = IdentityTest.nonEmpty.applyUnless(IdentityTest.empty.boolValue)(_.disableBoolValue)

    entity1 shouldBe IdentityTest.empty.enableBoolValue
    entity2 shouldBe IdentityTest.nonEmpty
    entity3 shouldBe IdentityTest.nonEmpty.disableBoolValue
  }

  "applyIf" should "apply onTrue if condition is true, otherwise onFalse" in {
    val entity1 = IdentityTest.empty.applyIf(_.boolValue)(_.disableBoolValue, _.enableBoolValue)
    val entity2 = IdentityTest.nonEmpty.applyIf(_.boolValue)(_.disableBoolValue, _.enableBoolValue)
    val entity3 = IdentityTest
      .nonEmpty
      .applyIf(IdentityTest.nonEmpty.boolValue)(_.disableBoolValue, _.enableBoolValue)

    entity1 shouldBe IdentityTest.empty.enableBoolValue
    entity2 shouldBe IdentityTest.nonEmpty.disableBoolValue
    entity3 shouldBe IdentityTest.nonEmpty.disableBoolValue
  }

  "applyIfTo" should "apply onTrue if condition is true, otherwise onFalse" in {
    val entity1 = IdentityTest.empty.applyIfTo(_.boolValue)(_.optionValue, _.optionValue)
    val entity2 = IdentityTest.nonEmpty.applyIfTo(_.boolValue)(_.optionValue, _.optionValue)
    val entity3 = IdentityTest
      .nonEmpty
      .applyIfTo(IdentityTest.nonEmpty.boolValue)(_.optionValue, _.optionValue)

    entity1 shouldBe None
    entity2 shouldBe Some(1)
    entity3 shouldBe Some(1)
  }

}
