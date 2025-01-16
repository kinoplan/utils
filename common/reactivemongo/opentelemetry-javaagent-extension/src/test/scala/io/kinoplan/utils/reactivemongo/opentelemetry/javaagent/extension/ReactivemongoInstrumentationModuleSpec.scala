package io.kinoplan.utils.reactivemongo.opentelemetry.javaagent.extension

import scala.concurrent.Future

import io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.api.trace.SpanKind
import io.opentelemetry.javaagent.testing.common.AgentTestingExporterAccess
import io.opentelemetry.sdk.trace.data.SpanData
import org.scalatest.{Assertion, BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName
import reactivemongo.api.AsyncDriver
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.bson.document

class ReactivemongoInstrumentationModuleSpec
    extends AsyncWordSpec
      with Matchers
      with BeforeAndAfterAll
      with BeforeAndAfterEach {

  var container: GenericContainer[Nothing] = _
  val driver: AsyncDriver = AsyncDriver()

  def tools: Future[BSONCollection] = driver
    .connect(s"mongodb://${container.getHost}:${container.getFirstMappedPort}")
    .flatMap(_.database("test"))
    .map(_.collection[BSONCollection]("tools"))

  override def beforeAll(): Unit = {
    super.beforeAll()

    val MONGODB_IMAGE = DockerImageName.parse("mongo:5.0")
    container = new GenericContainer(MONGODB_IMAGE).withExposedPorts(27017)
    container.start()
  }

  override def afterAll(): Unit = {
    container.stop()
    super.afterAll()
  }

  override protected def beforeEach(): Unit = {
    AgentTestingExporterAccess.reset()
    super.beforeEach()
  }

  private def checkHasException(span: SpanData): Assertion = span
    .getEvents
    .get(0)
    .getAttributes
    .get(AttributeKey.stringKey("exception.message")) should not be null

  private def checkSpan(span: SpanData)(
    dbOperationName: String,
    dbNamespace: String,
    extraChecks: SpanData => Assertion*
  ): Assertion = {
    span.getKind shouldBe SpanKind.CLIENT
    span.getAttributes.get(AttributeKey.stringKey("db.namespace")) shouldBe dbNamespace
    span.getAttributes.get(AttributeKey.stringKey("db.operation.name")) shouldBe dbOperationName
    span.getAttributes.get(AttributeKey.stringKey("db.system")) shouldBe "mongodb"
    extraChecks.map(_.apply(span))
    span.getAttributes.get(AttributeKey.stringKey("db.query.text")) should not be null
  }

  private def checkCollectionSpan(
    span: SpanData
  )(dbOperationName: String, extraChecks: SpanData => Assertion*): Assertion = checkSpan(span)(
    dbOperationName,
    "test",
    Seq[SpanData => Assertion](
      _.getName shouldBe s"$dbOperationName test.tools",
      _.getAttributes.get(AttributeKey.stringKey("db.collection.name")) shouldBe "tools"
    ) ++ extraChecks: _*
  )

  private def testOp(dbOperationName: String, extraChecks: SpanData => Assertion*)(
    op: => Future[Unit]
  ): Future[Assertion] = op.map { _ =>
    val spans = AgentTestingExporterAccess.getExportedSpans
    // we need only the last created span in common case
    val span = spans.get(spans.size() - 1)
    checkCollectionSpan(span)(dbOperationName, extraChecks: _*)
  }

  "Reactivemongo Instrumentation Module" should {

    "create spans for drop" in
      testOp("drop", checkHasException) {
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

    // TODO find out why it throws error
    /* "create spans for distinct" in {
      for {
        t <- tools
        _ <- t.distinct[BSONDocument, List]("name", None)
      } yield true shouldBe true
    }*/
  }

}
