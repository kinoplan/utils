package io.kinoplan.utils.reactivemongo.kamon.instrumentation

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import kamon.tag.Lookups.{plain, plainLong}
import kamon.testkit.{InitAndStopKamonAfterAll, TestSpanReporter}
import kamon.trace.Span
import org.scalatest.{Assertion, Canceled, Failed, OptionValues, Outcome, Retries}
import org.scalatest.concurrent.Eventually
import org.scalatest.matchers.should.Matchers
import org.scalatest.tagobjects.Retryable
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import org.scalatest.wordspec.AnyWordSpec
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.utility.DockerImageName
import reactivemongo.api.AsyncDriver
import reactivemongo.api.bson.{BSONDocument, document}
import reactivemongo.api.bson.collection.BSONCollection

class ReactiveMongoClientInstrumentationSpec
    extends AnyWordSpec
      with Matchers
      with Eventually
      with InitAndStopKamonAfterAll
      with OptionValues
      with TestSpanReporter
      with Retries {

  val maxRetryCount = 3

  override def withFixture(test: NoArgTest): Outcome =
    if (isRetryable(test)) withRetryOnFailure(withFixture(test, maxRetryCount))
    else super.withFixture(test)

  def withFixture(test: NoArgTest, count: Int): Outcome = {
    val outcome = super.withFixture(test)
    outcome match {
      case Failed(_) | Canceled(_) =>
        if (count == 1) super.withFixture(test)
        else withFixture(test, count - 1)
      case _ => outcome
    }
  }

  var container: MongoDBContainer = _
  val driver: AsyncDriver = AsyncDriver()

  def tools: Future[BSONCollection] = driver
    .connect(s"mongodb://${container.getHost}:${container.getFirstMappedPort}")
    .flatMap(_.database("test"))
    .map(_.collection[BSONCollection]("tools"))

  override def beforeAll(): Unit = {
    super.beforeAll()

    container = new MongoDBContainer(DockerImageName.parse("mongo:5.0")).withCommand(
      "mongod --replSet \"rs0\""
    )

    container.start()
  }

  override def afterAll(): Unit = {
    container.stop()
    super.afterAll()
  }

  private def checkSpan(
    dbOperationName: String,
    dbNamespace: String,
    extraChecks: Seq[Span.Finished => Assertion]
  ): Span.Finished = eventually(timeout(2.seconds)) {
    val span = testSpanReporter().nextSpan().value
    span.metricTags.get(plain("db.namespace")) shouldBe dbNamespace
    span.metricTags.get(plain("db.operation.name")) shouldBe dbOperationName
    span.metricTags.get(plain("db.system.name")) shouldBe "mongodb"
    span.tags.get(plain("db.query.text")) should not be null
    extraChecks.foreach(_.apply(span))
    span
  }

  private def checkCollectionSpan(
    dbOperationName: String,
    extraChecks: Seq[Span.Finished => Assertion] = Seq.empty
  ): Span.Finished = checkSpan(
    dbOperationName,
    "test",
    Seq[Span.Finished => Assertion](
      _.operationName shouldBe s"$dbOperationName tools",
      _.metricTags.get(plain("db.collection.name")) shouldBe "tools"
    ) ++ extraChecks
  )

  private def testOp(
    dbOperationName: String,
    extraChecks: Seq[Span.Finished => Assertion] = Seq.empty
  )(op: => Future[Unit]): Unit = {
    op
    checkCollectionSpan(dbOperationName, extraChecks)
    testSpanReporter().clear()
  }

  "ReactiveMongo Instrumentation" should {
    "create spans for drop" taggedAs Retryable in
      testOp("drop", Seq(_.hasError shouldBe true)) {
        for {
          t <- tools
          _ <- t.drop()
        } yield ()
      }
    "create spans for findAndUpdate" taggedAs Retryable in
      testOp("findAndModify") {
        for {
          t <- tools
          _ <- t.insert.one(document("name" -> "kamon", "findAndUpdate" -> true))
          _ <- t.findAndUpdate(
            document("name" -> "kamon", "findAndUpdate" -> true),
            document("$set" -> document("name" -> "zipkin"))
          )
        } yield ()
      }
    "create spans for findAndRemove" taggedAs Retryable in
      testOp("findAndModify") {
        for {
          t <- tools
          _ <- t.insert.one(document("name" -> "kamon", "findAndRemove" -> true))
          _ <- t.findAndRemove(document("name" -> "kamon", "findAndRemove" -> true))
        } yield ()
      }
    "create spans for findAndModify" taggedAs Retryable in
      testOp("findAndModify") {
        for {
          t <- tools
          _ <- t.insert.one(document("name" -> "kamon", "findAndModify" -> true))
          updateOp = t.updateModifier(document(f"$$set" -> document("name" -> "zipkin")))
          _ <- t.findAndModify(document("name" -> "kamon", "findAndModify" -> true), updateOp)
        } yield ()
      }

    "create spans for updateOne" taggedAs Retryable in
      testOp("update") {
        for {
          t <- tools
          _ <- t.insert.one(document("name" -> "kamon", "updateOne" -> true))
          _ <- t
            .update
            .one(
              document("name" -> "kamon", "updateOne" -> true),
              document("$set" -> document("name" -> "zipkin"))
            )
        } yield ()
      }

    "create spans for updateMany" taggedAs Retryable in
      testOp("update") {
        for {
          t <- tools
          _ <- t
            .insert
            .many(
              Seq(
                document("name" -> "kamon", "updateMany" -> "one"),
                document("name" -> "zipkin", "updateMany" -> "two")
              )
            )
          update = t.update(ordered = false)
          elements <- Future.sequence(
            List(
              update.element(
                document("updateMany" -> "one"),
                document("$set" -> document("name" -> "kamon updated"))
              ),
              update.element(
                document("updateMany" -> "two"),
                document("$set" -> document("name" -> "zipkin updated"))
              )
            )
          )
          _ <- update.many(elements)
        } yield ()
      }

    "create spans for insertOne" taggedAs Retryable in
      testOp("insert") {
        for {
          t <- tools
          _ <- t.insert.one(document("name" -> "kamon", "insertOne" -> true))
        } yield ()
      }

    "create spans for insertMany" taggedAs Retryable in
      testOp("insert") {
        for {
          t <- tools
          _ <- t
            .insert
            .many(
              Seq(
                document("name" -> "kamon", "insertMany" -> "one"),
                document("name" -> "kamon", "insertMany" -> "two"),
                document("name" -> "kamon", "insertMany" -> "three")
              )
            )
        } yield ()
      }

    "create spans for countDocuments" taggedAs Retryable in
      testOp("count") {
        for {
          t <- tools
          _ <- t
            .insert
            .many(
              Seq(
                document("name" -> "kamon", "count" -> true),
                document("name" -> "kamon", "count" -> true),
                document("name" -> "kamon", "count" -> true)
              )
            )
          _ <- t.count(Some(document("count" -> true)))
        } yield ()
      }

    "create spans for distinct" taggedAs Retryable in
      testOp("distinct") {
        for {
          t <- tools
          _ <- t.distinct[BSONDocument, List]("name", None)
        } yield ()
      }

    "create spans for aggregate" taggedAs Retryable in {

      for {
        t <- tools
        _ <- t
          .insert
          .many(
            Seq(
              document("name" -> "kamon", "aggregate" -> true),
              document("name" -> "zipkin", "aggregate" -> true),
              document("name" -> "datadog", "aggregate" -> true)
            )
          )
        _ <- t
          .aggregateWith(batchSize = Some(2)) { framework =>
            List(framework.Match(document("aggregate" -> true)))
          }
          .collect[List]()
      } yield ()

      checkCollectionSpan(
        "aggregate",
        Seq(_.tags.get(plainLong("db.response.returned_rows")) shouldBe 2L)
      )
      checkCollectionSpan(
        "getMore",
        Seq(_.tags.get(plainLong("db.response.returned_rows")) shouldBe 1L)
      )
      testSpanReporter().clear()
    }

    "create spans for find" taggedAs Retryable in {
      for {
        t <- tools
        _ <- t
          .insert
          .many(
            Seq(
              document("name" -> "kamon", "find" -> true),
              document("name" -> "zipkin", "find" -> true),
              document("name" -> "datadog", "find" -> true)
            )
          )
        _ <- t.find(document("find" -> true)).batchSize(2).cursor().collect[List]()
      } yield ()

      checkCollectionSpan("find", Seq(_.tags.get(plainLong("db.response.returned_rows")) shouldBe 2L))
      checkCollectionSpan(
        "getMore",
        Seq(_.tags.get(plainLong("db.response.returned_rows")) shouldBe 1L)
      )
      testSpanReporter().clear()
    }

    "create spans in transaction" taggedAs Retryable in {
      for {
        t <- tools
        db = t.db
        dbWithSession <- db.startSession()
        dbWithTx <- dbWithSession.startTransaction(None)
        txCollection = dbWithTx.collection[BSONCollection]("tools")
        _ <- txCollection
          .insert(ordered = true)
          .many(
            Seq(
              document("name" -> "kamon", "transaction" -> true),
              document("name" -> "zipkin", "transaction" -> true)
            )
          )
          .flatMap(result =>
            for {
              _ <- dbWithTx.commitTransaction()
              _ <- dbWithSession.endSession()
            } yield result
          )
          .recoverWith { case error: Throwable =>
            (
              for {
                _ <- dbWithTx.abortTransaction()
                _ <- dbWithSession.endSession()
              } yield ()
            ).flatMap(_ => Future.failed(error))
          }
      } yield ()

      checkSpan("startSession", "test", Seq(_.operationName shouldBe "startSession test"))
      checkCollectionSpan("insert")
      checkSpan("commitTransaction", "admin", Seq(_.operationName shouldBe "commitTransaction admin"))
      checkSpan("endSessions", "test", Seq(_.operationName shouldBe "endSessions test"))

      testSpanReporter().clear()
    }

  }

}
