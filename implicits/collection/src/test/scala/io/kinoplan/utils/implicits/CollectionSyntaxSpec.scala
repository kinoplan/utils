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
      assert(commons.diffBy(_.number)(List()) === List(test1, test2, test3, test4))
      assert(commons.diffBy(_.number)(List(1, 2)) === List(test3, test4))
      assert(commons.diffBy(_.number)(List(3, 4)) === List(test1, test2))
    }
  }

  "CollectionHelper#diffByMerge" should {
    "return correct value" in {
      assert(commons.diffByMerge(_.number)(List()) === List(test1, test2, test3, test4))
      assert(commons.diffByMerge(_.number)(List(test5, test6)) === commons ++ List(test5, test6))
      assert(
        commons.diffByMerge(_.number)(List(test5, test6, test7)) === commons ++ List(test5, test6)
      )
    }
  }

  "CollectionHelper#distinctBy" should {
    "return correct value" in {
      assert(commons.distinctBy(_.number) === List(test1, test2, test3))
      assert(commons.distinctBy(_.text) === List(test1, test2, test3, test4))
    }
  }

  "CollectionHelper#filterIf" should {
    "return correct value" in {
      assert(commons.filterIf(cond = true)(_.number == 1) === List(test1))
      assert(commons.filterIf(cond = true)(_.number > 2) === List(test3, test4))
      assert(commons.filterIf(cond = false)(_.number > 2) === commons)
    }
  }

  "CollectionHelper#mapIf" should {
    "return correct value" in {
      assert(commons.mapIf(_.number == 1)(_ => test5) === List(test5, test2, test3, test4))
      assert(commons.mapIf(_.number == 4)(_ => test5) === commons)
      assert(commons.mapIf(_.number == 3)(_ => test6) === List(test1, test2, test6, test6))
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
      assert(commons.intersectByMerge(_.number)(List()) === List())
      assert(commons.intersectByMerge(_.number)(List(test1, test2)) === List(test1, test2))
      assert(commons.intersectByMerge(_.number)(List(test3, test4, test7)) === List(test3, test4))
    }
  }

  "CollectionHelper#maxOption" should {
    "return correct value" in {
      assert(commons.map(_.number).maxOption.contains(test4.number))
      assert(List.empty[Int].maxOption.isEmpty)
    }
  }

  "CollectionHelper#maxByOption" should {
    "return correct value" in {
      assert(commons.maxByOption(_.number).contains(test3))
      assert(commons.maxByOption(_.text).contains(test2))
      assert(List.empty[ClassForTest].maxByOption(_.number).isEmpty)
    }
  }

  "CollectionHelper#minOption" should {
    "return correct value" in {
      assert(commons.map(_.number).minOption.contains(test1.number))
      assert(List.empty[Int].minOption.isEmpty)
    }
  }

  "CollectionHelper#minByOption" should {
    "return correct value" in {
      assert(commons.minByOption(_.number).contains(test1))
      assert(commons.minByOption(_.text).contains(test1))
      assert(List.empty[ClassForTest].minByOption(_.number).isEmpty)
    }
  }

  "CollectionHelper#sumBy" should {
    "return correct value" in commons.sumBy(_.number) === 9
  }

  "CollectionHelper#zipWith" should {
    "return correct value" in {
      assert(
        commons.zipWith[Int](_.number) === Map[Int, ClassForTest](1 -> test1, 2 -> test2, 3 -> test4)
      )
      the[NoSuchElementException] thrownBy commons.zipWith[Int](_.number)(5)
    }
  }

  "CollectionHelper#zipBoth" should {
    "return correct value" in {
      assert(
        commons.zipBoth[Int, String](_.number, _.text) ===
          Map(
            test1.number -> test1.text,
            test2.number -> test2.text,
            test3.number -> test3.text,
            test4.number -> test4.text
          )
      )
      the[NoSuchElementException] thrownBy commons.zipBoth[Int, String](_.number, _.text)(5)
    }
  }

  "CollectionHelper#zipWithDefaultValue" should {
    "return correct value" in {
      assert(
        commons.zipWithDefaultValue[Int](_.number)(test2) === Map(1 -> test1, 2 -> test2, 3 -> test4)
      )
      assert(commons.zipWithDefaultValue[Int](_.number)(test2)(5) === test2)
    }

    "CollectionHelper#zipBothWithDefaultValue" should {
      "return correct value" in {
        assert(
          commons.zipBothWithDefaultValue[Int, String](_.number, _.text)(test2.text) ===
            Map(
              test1.number -> test1.text,
              test2.number -> test2.text,
              test3.number -> test3.text,
              test4.number -> test4.text
            )
        )
        assert(
          commons.zipBothWithDefaultValue[Int, String](_.number, _.text)(test2.text)(5) ===
            test2.text
        )
      }
    }
  }

}
