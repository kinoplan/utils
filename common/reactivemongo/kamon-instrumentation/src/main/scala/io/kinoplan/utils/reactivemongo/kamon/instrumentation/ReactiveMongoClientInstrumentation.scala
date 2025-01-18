package io.kinoplan.utils.reactivemongo.kamon.instrumentation

import kanela.agent.api.instrumentation.InstrumentationBuilder
import reactivemongo.api._

class ReactiveMongoClientInstrumentation extends InstrumentationBuilder {

  onType("reactivemongo.api.MongoConnection").advise(
    method("sendExpectingResponse"),
    classOf[MongoConnectionSendExpectingResponseAdvice]
  )

}
