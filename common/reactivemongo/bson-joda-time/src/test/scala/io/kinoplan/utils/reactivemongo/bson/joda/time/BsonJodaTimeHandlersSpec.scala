package io.kinoplan.utils.reactivemongo.bson.joda.time

import org.scalatest.flatspec.AnyFlatSpec

import io.kinoplan.utils.reactivemongo.bson.joda.time.behaviors.ScenarioBehaviors
import io.kinoplan.utils.reactivemongo.bson.joda.time.models.cases._

class BsonJodaTimeHandlersSpec extends AnyFlatSpec with ScenarioBehaviors {
  TestCase1.description should behave like commonScenario(TestCase1.scenario())
  TestCase2.description should behave like commonScenario(TestCase2.scenario())
  TestCase3.description should behave like commonScenario(TestCase3.scenario())
  TestCase4.description should behave like commonScenario(TestCase4.scenario())
  TestCase5.description should behave like commonScenario(TestCase5.scenario())
  TestCase6.description should behave like commonScenario(TestCase6.scenario())
  TestCase7.description should behave like commonScenario(TestCase7.scenario())
}
