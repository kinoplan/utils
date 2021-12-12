package io.kinoplan.utils.zio.reactivemongo.api.driver

import scala.concurrent.duration.DurationInt

import izumi.distage.model.definition.Lifecycle
import reactivemongo.api.AsyncDriver
import zio.{IO, ZManaged}

class AsyncDriverResource
    extends Lifecycle.OfZIO(ZManaged.make(IO(AsyncDriver()))(driver =>
      IO.fromFuture(implicit ec => driver.close(10.seconds)).orDie.unit
    ))
