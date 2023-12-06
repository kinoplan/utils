package io.kinoplan.utils.nullable.core

import org.scalatest.wordspec.AnyWordSpec

class NullableSpec extends AnyWordSpec {
  val absentValue: Nullable[Nothing] = Nullable.Absent
  val nullValue: Nullable[Nothing] = Nullable.Null
  val value: Nullable[Int] = Nullable.NonNull(1)

  "Nullable#fold" should {
    "return correct behavior" in {
      assert(!absentValue.fold(false, false, _ => true))
      assert(!nullValue.fold(false, false, _ => true))
      assert(value.fold(false, false, _ => true))
    }
  }

  "Nullable#foldPresent" should {
    "return correct behavior" in {
      assert(absentValue.foldPresent(_.isEmpty).isEmpty)
      assert(nullValue.foldPresent(_.isEmpty).contains(true))
      assert(value.foldPresent(_.get).contains(1))
    }
  }

  "Nullable#exists" should {
    "return correct behavior" in {
      assert(!absentValue.exists(_ == 1))
      assert(!nullValue.exists(_ == 1))
      assert(value.exists(_ == 1))
    }
  }

  "Nullable#map" should {
    "return correct behavior" in {
      assert(absentValue.map(_.toString) == Nullable.Absent)
      assert(nullValue.map(_.toString) == Nullable.Null)
      assert(value.map(_ + 1) == Nullable.NonNull(2))
    }
  }

  "Nullable#flatMap" should {
    "return correct behavior" in {
      assert(absentValue.flatMap(_ => Nullable.Absent) == Nullable.Absent)
      assert(nullValue.flatMap(_ => Nullable.Null) == Nullable.Null)
      assert(value.flatMap(v => Nullable.NonNull(v + 2)) == Nullable.NonNull(3))
    }
  }

  "Nullable#orElse" should {
    "return correct behavior" in {
      assert(absentValue.orElse(Nullable.Null) == Nullable.Null)
      assert(nullValue.orElse(Nullable.Absent) == Nullable.Absent)
      assert(value.orElse(Nullable.NonNull(2)) == Nullable.NonNull(1))
    }
  }

  "Nullable#toOption" should {
    "return correct behavior" in {
      assert(absentValue.toOption.isEmpty)
      assert(nullValue.toOption.isEmpty)
      assert(value.toOption.contains(1))
    }
  }

  "Nullable#toOptionOption" should {
    "return correct behavior" in {
      assert(absentValue.toOptionOption.isEmpty)
      assert(nullValue.toOptionOption.exists(_.isEmpty))
      assert(value.toOptionOption.contains(Some(1)))
    }
  }

  "Nullable#isNull" should {
    "return correct behavior" in {
      assert(!absentValue.isNull)
      assert(nullValue.isNull)
      assert(!value.isNull)
    }
  }

  "Nullable#isAbsent" should {
    "return correct behavior" in {
      assert(absentValue.isAbsent)
      assert(!nullValue.isAbsent)
      assert(!value.isAbsent)
    }
  }

  "Nullable#isPresent" should {
    "return correct behavior" in {
      assert(!absentValue.isPresent)
      assert(!nullValue.isPresent)
      assert(value.isPresent)
    }
  }

  "Nullable#orNull" should {
    "return correct behavior" in {
      assert(Nullable.orNull(absentValue.toOption) == Nullable.Null)
      assert(Nullable.orNull(nullValue.toOption) == Nullable.Null)
      assert(Nullable.orNull(value.toOption) == value)
    }
  }

  "Nullable#orAbsent" should {
    "return correct behavior" in {
      assert(Nullable.orAbsent(absentValue.toOption) == Nullable.Absent)
      assert(Nullable.orAbsent(nullValue.toOption) == Nullable.Absent)
      assert(Nullable.orAbsent(value.toOption) == value)
    }
  }

}
