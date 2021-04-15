package io.kinoplan.utils.wrappers.base.logging.context

import kit.data.TestKitConstants
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

import io.kinoplan.utils.wrappers.base.logging.Loggable

class MapContextSpec
    extends AnyWordSpec with MockitoSugar with Matchers with Loggable with TestKitConstants {
  "MapContext#isEmpty" should {
    "check call method with empty value" in assert(MapContext.empty.isEmpty)
    "check call method with non empty value" in assert(!mapContext.isEmpty)
  }

  "MapContext#get" should {
    "check call method with empty value" in assert(MapContext.empty.get("arg1").isEmpty)
    "check call method with non empty value" in assert(mapContext.get("arg1").contains("arg1"))
  }

  "MapContext#put" should {
    "check call method with option value" in assert(
      MapContext.empty.put(("arg1", Some(1))).underlyingMap == MapContext(Map("arg1" -> Some(1)))
        .underlyingMap
    )
    "check call method with common value" in assert(
      MapContext.empty.put(("arg1", 1)).underlyingMap == MapContext(Map("arg1" -> 1)).underlyingMap
    )
  }

  "MapContext#remove" should {
    "check call method with valid key" in {
      val testMapContext = MapContext(Map("arg1" -> 1))

      testMapContext.remove("arg1").map { result =>
        assert(result == "1")
        assert(testMapContext.remove("arg2").isEmpty)
      }
    }
    "check call method with invalid key" in {
      val testMapContext = MapContext(("arg1", "1"))

      assert(testMapContext.remove("arg2").isEmpty)
      testMapContext.remove("arg2")
      assert(testMapContext.underlyingMap == MapContext(Map("arg1" -> "1")).underlyingMap)
    }
  }

  "MapContext#clear" should {
    "check value isEmpty after call method" in {
      val testMapContext = MapContext(("arg1", "1"))

      testMapContext.clear()

      assert(testMapContext.isEmpty)
    }
  }
}
