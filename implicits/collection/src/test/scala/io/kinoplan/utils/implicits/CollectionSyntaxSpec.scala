package io.kinoplan.utils.implicits

import org.scalatest.wordspec.AnyWordSpec

class CollectionSyntaxSpec extends AnyWordSpec with CollectionSyntax {
  import CollectionSyntaxSpec._

  "CollectionHelper#intersectBy" should {
    "return correct value" in {
      assert(commons.intersectBy(_.number)(List()) === List())
      assert(commons.intersectBy(_.number)(List(2, 1)) === List(test2, test1))
      assert(commons.intersectBy(_.number)(List(3, 4)) === List(test3, test4))
    }
  }
}

object CollectionSyntaxSpec {
  case class ClassForTest(number: Int, text: String)

  val test1 = ClassForTest(1, "Number One")
  val test2 = ClassForTest(2, "Number Two")
  val test3 = ClassForTest(3, "Number Three")
  val test4 = ClassForTest(3, "Number Three (but Four)")
  val test5 = ClassForTest(5, "Number Five")
  val test6 = ClassForTest(6, "Number Six")
  val test7 = ClassForTest(3, "Number Seven (but Three)")

  val commons = List(test1, test2, test3, test4)
}
