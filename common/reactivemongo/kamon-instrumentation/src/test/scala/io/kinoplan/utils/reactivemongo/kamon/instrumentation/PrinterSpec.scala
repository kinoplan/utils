package io.kinoplan.utils.reactivemongo.kamon.instrumentation

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import reactivemongo.api.bson.{BSONDocument, document}
import reactivemongo.api.bson.BSONDocument.pretty

class PrinterSpec extends AnyWordSpec with Matchers {

  val testBSONDocument: BSONDocument = document(
    "key_1" -> "value_1",
    "array" -> List(document("elem" -> 1), document("elem" -> 2)),
    "nested" -> document("nested_key" -> document("nested_key_2" -> "value"))
  )

  "Printer" should {
    "return the same as pretty" in {
      Printer.withLimit(1000).print(testBSONDocument) shouldBe pretty(testBSONDocument)
    }
    "return truncated string" in {
      Printer.withLimit(10).print(testBSONDocument).length shouldBe 10
    }
  }

}
