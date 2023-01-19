package io.kinoplan.utils.zio

import zio.{Tag, Task, ZEnvironment}

import io.kinoplan.utils.IntegrationCheck

trait ZIntegration[A] {
  def module: ZEnvironment[A]
  def moduleCheck: ZEnvironment[Set[IntegrationCheck[Task]]]
}

object ZIntegration {

  def make[A: Tag](moduleImpl: A, moduleCheckImpl: Set[IntegrationCheck[Task]]): ZIntegration[A] =
    new ZIntegration[A] {
      def module: ZEnvironment[A] = ZEnvironment(moduleImpl)

      def moduleCheck: ZEnvironment[Set[IntegrationCheck[Task]]] = ZEnvironment(moduleCheckImpl)
    }

  def environment[A: Tag](
    moduleImpl: A,
    moduleCheckImpl: Set[IntegrationCheck[Task]]
  ): ZEnvironment[ZIntegration[A]] = ZEnvironment(make(moduleImpl, moduleCheckImpl))

}
