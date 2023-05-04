package io.kinoplan.utils.zio.redisson

import org.redisson.config.Config

import io.kinoplan.utils.zio.redisson.codec.RCodec
import io.kinoplan.utils.zio.redisson.utils.IdentitySyntax.syntaxIdentityOps

package object module {

  private[redisson] def setupRedissonConfig[K, V](
    config: Config,
    configurator: Config => Config = identity
  )(implicit
    codec: RCodec[K, V]
  ) = configurator(config).applyOption(codec.underlying)((builder, value) => builder.setCodec(value))

}
