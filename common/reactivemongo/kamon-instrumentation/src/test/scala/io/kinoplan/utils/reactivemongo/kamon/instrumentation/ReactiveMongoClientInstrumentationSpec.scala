package io.kinoplan.utils.reactivemongo.kamon.instrumentation

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import kamon.tag.Lookups.{plain, plainLong}
import kamon.testkit.{InitAndStopKamonAfterAll, TestSpanReporter}
import kamon.trace.Span
import org.scalatest.{Assertion, OptionValues}
import org.scalatest.concurrent.Eventually
import org.scalatest.matchers.should.Matchers
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
      with TestSpanReporter {

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
    extraChecks: Span.Finished => Assertion*
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
    extraChecks: Span.Finished => Assertion*
  ): Span.Finished = checkSpan(
    dbOperationName,
    "test",
    Seq[Span.Finished => Assertion](
      _.operationName shouldBe s"$dbOperationName tools",
      _.metricTags.get(plain("db.collection.name")) shouldBe "tools"
    ) ++ extraChecks: _*
  )

  private def testOp(dbOperationName: String, extraChecks: Span.Finished => Assertion*)(
    op: => Future[Unit]
  ): Unit = {
    op
    checkCollectionSpan(dbOperationName, extraChecks: _*)
    testSpanReporter().clear()
  }

  "ReactiveMongo Instrumentation" should {
    "create spans for drop" in
      testOp("drop", _.hasError shouldBe true) {
        for {
          t <- tools
          _ <- t.drop()
        } yield ()
      }
    "create spans for findAndUpdate" in
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
    "create spans for findAndRemove" in
      testOp("findAndModify") {
        for {
          t <- tools
          _ <- t.insert.one(document("name" -> "kamon", "findAndRemove" -> true))
          _ <- t.findAndRemove(document("name" -> "kamon", "findAndRemove" -> true))
        } yield ()
      }
    "create spans for findAndModify" in
      testOp("findAndModify") {
        for {
          t <- tools
          _ <- t.insert.one(document("name" -> "kamon", "findAndModify" -> true))
          updateOp = t.updateModifier(document(f"$$set" -> document("name" -> "zipkin")))
          _ <- t.findAndModify(document("name" -> "kamon", "findAndModify" -> true), updateOp)
        } yield ()
      }

    "create spans for updateOne" in
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

    "create spans for updateMany" in
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

    "create spans for insertOne" in
      testOp("insert") {
        for {
          t <- tools
          _ <- t.insert.one(document("name" -> "kamon", "insertOne" -> true))
        } yield ()
      }

    "create spans for insertMany" in
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

    "create spans for countDocuments" in
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

    "create spans for distinct" in
      testOp("distinct") {
        for {
          t <- tools
          _ <- t.distinct[BSONDocument, List]("name", None)
        } yield ()
      }

    "create spans for aggregate" in {

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
        _.tags.get(plainLong("db.response.returned_rows")) shouldBe 2L
      )
      checkCollectionSpan("getMore", _.tags.get(plainLong("db.response.returned_rows")) shouldBe 1L)
      testSpanReporter().clear()
    }

    "create spans for find" in {
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

      checkCollectionSpan("find", _.tags.get(plainLong("db.response.returned_rows")) shouldBe 2L)
      checkCollectionSpan("getMore", _.tags.get(plainLong("db.response.returned_rows")) shouldBe 1L)
      testSpanReporter().clear()
    }

    "create spans in transaction" in {
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

      checkSpan("startSession", "test", _.operationName shouldBe "startSession test")
      checkCollectionSpan("insert")
      checkSpan("commitTransaction", "admin", _.operationName shouldBe "commitTransaction admin")
      checkSpan("endSessions", "test", _.operationName shouldBe "endSessions test")

      testSpanReporter().clear()
    }

  }

}
