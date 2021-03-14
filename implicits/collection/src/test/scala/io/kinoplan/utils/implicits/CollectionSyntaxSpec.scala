package io.kinoplan.utils.implicits

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import io.kinoplan.utils.implicits.CollectionSyntax.syntaxCollectionOps

class CollectionSyntaxSpec extends AnyWordSpec with Matchers {

  case class ClassForTest(number: Int, text: String)

  val test1 = ClassForTest(1, "Number One")
  val test2 = ClassForTest(2, "Number Two")
  val test3 = ClassForTest(3, "Number Three")
  val test4 = ClassForTest(3, "Number Three (but Four)")
  val test5 = ClassForTest(5, "Number Five")
  val test6 = ClassForTest(6, "Number Six")
  val test7 = ClassForTest(3, "Number Seven (but Three)")

  val commons = List(test1, test2, test3, test4)

  "CollectionHelper#diffBy" should {
    "return correct value" in {
      commons.diffBy(_.number)(List()) === List(test1, test2, test3, test4)
      commons.diffBy(_.number)(List(1, 2)) === List(test3, test4)
      commons.diffBy(_.number)(List(3, 4)) === List(test1, test2)
    }
  }

  "CollectionHelper#diffByMerge" should {
    "return correct value" in {
      commons.diffByMerge(_.number)(List()) === List(test1, test2, test3, test4)
      commons.diffByMerge(_.number)(List(test5, test6)) === commons ++ List(test5, test6)
      commons.diffByMerge(_.number)(List(test5, test6, test7)) === commons ++ List(test5, test6)
    }
  }

  "CollectionHelper#distinctBy" should {
    "return correct value" in {
      commons.distinctBy(_.number) === List(test1, test2, test3)
      commons.distinctBy(_.text) === List(test1, test2, test3, test4)
    }
  }

  "CollectionHelper#filterIf" should {
    "return correct value" in {
      commons.filterIf(cond = true)(_.number == 1) === List(test1)
      commons.filterIf(cond = true)(_.number > 2) === List(test3, test4)
      commons.filterIf(cond = false)(_.number > 2) === commons
    }
  }

  "CollectionSyntax#intersectBy" should {
    "return correct value" in {
      assert(commons.intersectBy(_.number)(List()) === List())
      assert(commons.intersectBy(_.number)(List(1, 2)) === List(test1, test2))
      assert(commons.intersectBy(_.number)(List(3, 4)) === List(test3, test4))
    }
  }

  "CollectionHelper#intersectByMerge" should {
    "return correct value" in {
      commons.intersectByMerge(_.number)(List()) === List()
      commons.intersectByMerge(_.number)(List(test1, test2)) === List(test1, test2)
      commons.intersectByMerge(_.number)(List(test3, test4, test5)) === List(test3, test4)
    }
  }

  "CollectionHelper#maxOption" should {
    "return correct value" in {
      commons.map(_.number).maxOption.contains(test4.number) === true
      List.empty[Int].maxOption.isEmpty === true
    }
  }

  "CollectionHelper#maxByOption" should {
    "return correct value" in {
      commons.maxByOption(_.number).contains(test4) === true
      commons.maxByOption(_.text).contains(test4) === true
      List.empty[ClassForTest].maxByOption(_.number).isEmpty === true
    }
  }

  "CollectionHelper#minOption" should {
    "return correct value" in {
      commons.map(_.number).minOption.contains(test1.number) === true
      List.empty[Int].minOption.isEmpty === true
    }
  }

  "CollectionHelper#minByOption" should {
    "return correct value" in {
      commons.minByOption(_.number).contains(test1) === true
      commons.minByOption(_.text).contains(test1) === true
      List.empty[ClassForTest].minByOption(_.number).isEmpty === true
    }
  }

  "CollectionHelper#sumBy" should { "return correct value" in commons.sumBy(_.number) === 9 }

  "CollectionHelper#zipWith" should {
    "return correct value" in {
      commons.zipWith(_.number) === Map(1 -> test1, 2 -> test2, 3 -> test4)
      the[NoSuchElementException] thrownBy commons.zipWith(_.number)(5)
    }
  }

  "CollectionHelper#zipBoth" should {
    "return correct value" in {
      commons.zipBoth(_.number, _.text) === Map(
        test1.number -> test1.text,
        test2.number -> test2.text,
        test3.number -> test3.text,
        test4.number -> test4.text
      )
      the[NoSuchElementException] thrownBy commons.zipBoth(_.number, _.text)(5)
    }
  }

  "CollectionHelper#zipWithDefaultValue" should {
    "return correct value" in {
      commons.zipWithDefaultValue(_.number)(test2) === Map(1 -> test1, 2 -> test2, 3 -> test4)
      commons.zipWithDefaultValue(_.number)(test2)(5) === test2
    }

    "CollectionHelper#zipBothWithDefaultValue" should {
      "return correct value" in {
        commons.zipBothWithDefaultValue(_.number, _.text)(test2.text) === Map(
          test1.number -> test1.text,
          test2.number -> test2.text,
          test3.number -> test3.text,
          test4.number -> test4.text
        )
        commons.zipBothWithDefaultValue(_.number, _.text)(test2.text)(5) === test2.text
      }
    }
  }
}
