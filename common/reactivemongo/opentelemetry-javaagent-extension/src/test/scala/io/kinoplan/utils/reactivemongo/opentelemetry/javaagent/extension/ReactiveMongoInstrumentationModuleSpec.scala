package io.kinoplan.utils.reactivemongo.opentelemetry.javaagent.extension

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, FiniteDuration, SECONDS}
import scala.jdk.CollectionConverters._

import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.api.trace.{Span, SpanContext, SpanKind, StatusCode, TraceFlags, TraceState}
import io.opentelemetry.javaagent.testing.common.AgentTestingExporterAccess
import io.opentelemetry.sdk.trace.data.SpanData
import org.scalatest.{Assertion, BeforeAndAfterAll, BeforeAndAfterEach, OptionValues}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.utility.DockerImageName
import reactivemongo.api.AsyncDriver
import reactivemongo.api.bson.{BSONDocument, document}
import reactivemongo.api.bson.collection.BSONCollection

// MongoDB 6.0+ is sent commands with OP_MSG, earlier versions - with OP_QUERY
abstract class ReactiveMongoInstrumentationModuleSpec(mongoVersion: String)
    extends AnyWordSpec
      with Matchers
      with OptionValues
      with BeforeAndAfterAll
      with BeforeAndAfterEach {

  // see Test / javaOptions
  private val queryTextMaxLength = 1024

  private val awaitTimeout: FiniteDuration = Duration(10, SECONDS)

  var container: MongoDBContainer = _
  val driver: AsyncDriver = AsyncDriver()

  def tools: Future[BSONCollection] = driver
    .connect(s"mongodb://${container.getHost}:${container.getFirstMappedPort}")
    .flatMap(_.database("test"))
    .map(_.collection[BSONCollection]("tools"))

  override def beforeAll(): Unit = {
    super.beforeAll()

    container = new MongoDBContainer(DockerImageName.parse(s"mongo:$mongoVersion")).withCommand(
      "mongod --replSet \"rs0\""
    )

    container.start()
  }

  override def afterAll(): Unit = {
    Await.ready(driver.close(), awaitTimeout)
    container.stop()
    super.afterAll()
  }

  override protected def beforeEach(): Unit = {
    AgentTestingExporterAccess.reset()
    super.beforeEach()
  }

  private def stringAttribute(span: SpanData, key: String): String = span
    .getAttributes
    .get(AttributeKey.stringKey(key))

  private def returnedRows(span: SpanData): Long = {
    val value: java.lang.Long = span
      .getAttributes
      .get(AttributeKey.longKey("db.response.returned_rows"))
    value.longValue()
  }

  private def checkSpan(
    spanName: String,
    dbOperationName: String,
    dbNamespace: String,
    extraChecks: Seq[SpanData => Assertion] = Seq.empty
  ): SpanData = {
    val span = AgentTestingExporterAccess.getExportedSpans.asScala.find(_.getName == spanName).value
    span.getKind shouldBe SpanKind.CLIENT
    stringAttribute(span, "db.system.name") shouldBe "mongodb"
    stringAttribute(span, "db.namespace") shouldBe dbNamespace
    stringAttribute(span, "db.operation.name") shouldBe dbOperationName
    stringAttribute(span, "db.query.text") should not be null
    extraChecks.foreach(_.apply(span))
    span
  }

  private def checkCollectionSpan(
    dbOperationName: String,
    extraChecks: Seq[SpanData => Assertion] = Seq.empty
  ): SpanData = checkSpan(
    s"$dbOperationName tools",
    dbOperationName,
    "test",
    Seq[SpanData => Assertion](stringAttribute(_, "db.collection.name") shouldBe "tools") ++
      extraChecks
  )

  private def testOp(dbOperationName: String, extraChecks: Seq[SpanData => Assertion] = Seq.empty)(
    op: => Future[Unit]
  ): Unit = {
    Await.result(op, awaitTimeout)
    checkCollectionSpan(dbOperationName, extraChecks)
    ()
  }

  s"ReactiveMongo Instrumentation Module with MongoDB $mongoVersion" should {
    "create spans for drop" in
      testOp("drop") {
        for {
          t <- tools
          _ <- t.drop(failIfNotFound = false)
        } yield ()
      }

    "mark failed commands as errors" in
      testOp("find", Seq(_.getStatus.getStatusCode shouldBe StatusCode.ERROR)) {
        tools.flatMap(_.find(document("$unknown" -> true)).one[BSONDocument]).failed.map(_ => ())
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
      testOp(
        "insert",
        Seq(_.getAttributes.get(AttributeKey.longKey("db.response.returned_rows")) shouldBe null)
      ) {
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
          _ <- t.distinct[String, List]("name", None)
        } yield ()
      }

    "create spans for aggregate" in {
      Await.result(
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
        } yield (),
        awaitTimeout
      )

      checkCollectionSpan("aggregate", Seq(returnedRows(_) shouldBe 2L))
      checkCollectionSpan("getMore", Seq(returnedRows(_) shouldBe 1L))
    }

    "create spans for find" in {
      Await.result(
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
        } yield (),
        awaitTimeout
      )

      checkCollectionSpan("find", Seq(returnedRows(_) shouldBe 2L))
      checkCollectionSpan("getMore", Seq(returnedRows(_) shouldBe 1L))
    }

    "create spans in transaction" in {
      Await.result(
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
          _ <- dbWithTx.commitTransaction()
          _ <- dbWithSession.endSession()
        } yield (),
        awaitTimeout
      )

      checkSpan("startSession test", "startSession", "test")
      checkCollectionSpan("insert")
      checkSpan("commitTransaction admin", "commitTransaction", "admin")
      checkSpan("endSessions test", "endSessions", "test")
    }

    "limit query text length" in {
      Await.result(
        for {
          t <- tools
          _ <- t.insert.one(document("name" -> "x" * 100000, "limit" -> true))
        } yield (),
        awaitTimeout
      )

      checkCollectionSpan(
        "insert",
        Seq(stringAttribute(_, "db.query.text").length shouldBe queryTextMaxLength)
      )
    }

    // The text of a command larger than the limit is truncated, while $db (OP_MSG),
    // which follows the documents, is still found (checked by checkCollectionSpan)
    "limit query text length of a large command" in {
      Await.result(
        for {
          t <- tools
          _ <-
            t.insert.many((0 until 20000).map(i => document("name" -> s"symbol $i", "large" -> true)))
        } yield (),
        awaitTimeout
      )

      checkCollectionSpan(
        "insert",
        Seq(
          stringAttribute(_, "db.query.text") should startWith("{\n  'insert': "),
          stringAttribute(_, "db.query.text").length shouldBe queryTextMaxLength
        )
      )
    }

    "propagate parent context" in {
      val collection = Await.result(tools, awaitTimeout)
      val parent = GlobalOpenTelemetry.getTracer("test").spanBuilder("parent").startSpan()
      val scope = parent.makeCurrent()
      val result =
        try collection.insert.one(document("name" -> "kamon", "parent" -> true))
        finally scope.close()
      Await.result(result, awaitTimeout)
      parent.end()

      checkCollectionSpan("insert", Seq(_.getParentSpanId shouldBe parent.getSpanContext.getSpanId))
    }

    "not export spans of a not sampled parent" in {
      val collection = Await.result(tools, awaitTimeout)
      val parent = Span.wrap(
        SpanContext.create(
          "0af7651916cd43dd8448eb211c80319c",
          "b7ad6b7169203331",
          TraceFlags.getDefault,
          TraceState.getDefault
        )
      )
      val scope = parent.makeCurrent()
      val result =
        try collection.insert.one(document("name" -> "kamon", "sampled" -> false))
        finally scope.close()
      Await.result(result, awaitTimeout)
      Await.result(
        collection.insert.one(document("name" -> "kamon", "sampled" -> true)),
        awaitTimeout
      )

      checkCollectionSpan("insert", Seq(_.getParentSpanContext.isValid shouldBe false))
      AgentTestingExporterAccess
        .getExportedSpans
        .asScala
        .count(_.getName == "insert tools") shouldBe 1
    }

    "propagate parent context to sequential operations" in {
      val collection = Await.result(tools, awaitTimeout)
      val parent = GlobalOpenTelemetry.getTracer("test").spanBuilder("parent").startSpan()
      val scope = parent.makeCurrent()
      val result =
        try for {
          _ <- collection.insert.one(document("name" -> "kamon", "sequential" -> true))
          _ <- collection.find(document("sequential" -> true)).one[BSONDocument]
          _ <- collection.count(Some(document("sequential" -> true)))
        } yield ()
        finally scope.close()
      Await.result(result, awaitTimeout)
      parent.end()

      Seq("insert", "find", "count").foreach(op =>
        checkCollectionSpan(op, Seq(_.getParentSpanId shouldBe parent.getSpanContext.getSpanId))
      )
    }

  }

}

class ReactiveMongoInstrumentationModuleMongo5Spec
    extends ReactiveMongoInstrumentationModuleSpec("5.0")

class ReactiveMongoInstrumentationModuleMongo7Spec
    extends ReactiveMongoInstrumentationModuleSpec("7.0")
