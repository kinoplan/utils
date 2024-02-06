package io.kinoplan.utils.reactivemongo.bson.joda.time.behaviors

import org.scalatest.flatspec.AnyFlatSpec

import io.kinoplan.utils.reactivemongo.bson.joda.time.models.TestScenario

trait ScenarioBehaviors {
  this: AnyFlatSpec =>

  def commonScenario[T](testScenario: TestScenario[T]): Unit = {
    it should "return correct value writeTry" in
      assert(testScenario.handler.writeTry(testScenario.data).toOption.contains(testScenario.bson))
    it should "return correct value readTry" in
      assert(testScenario.handler.readTry(testScenario.bson).toOption.contains(testScenario.data))
    it should "return incorrect value readTry" in
      assert(
        testScenario
          .handler
          .readTry(testScenario.bsonIncorrect)
          .failed
          .toOption
          .map(_.getMessage)
          .contains(testScenario.exceptionMessage)
      )
  }

}
