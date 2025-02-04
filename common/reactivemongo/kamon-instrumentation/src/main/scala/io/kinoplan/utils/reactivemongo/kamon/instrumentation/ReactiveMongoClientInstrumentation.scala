package io.kinoplan.utils.reactivemongo.kamon.instrumentation

import com.typesafe.config.Config
import kamon.Kamon
import kanela.agent.api.instrumentation.InstrumentationBuilder
import reactivemongo.api._

class ReactiveMongoClientInstrumentation extends InstrumentationBuilder {

  onType("reactivemongo.api.MongoConnection").advise(
    method("sendExpectingResponse"),
    classOf[MongoConnectionSendExpectingResponseAdvice]
  )

}

object ReactiveMongoClientInstrumentation {

  private val settings: Settings = readSettings(Kamon.config())

  val printer: Printer = Printer.withLimit(settings.maxNormalizedQueryLength)

  private def readSettings(config: Config): Settings = {
    val reactivemongoConfig = config.getConfig("kamon.instrumentation.reactivemongo")
    Settings(maxNormalizedQueryLength =
      reactivemongoConfig.getInt("tracing.max-normalized-query-length")
    )
  }

  private case class Settings(maxNormalizedQueryLength: Int)
}
