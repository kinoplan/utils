package io.kinoplan.utils.zio.redisson

import io.kinoplan.utils.zio.redisson.codec.RCodec
import io.kinoplan.utils.zio.redisson.utils.IdentitySyntax.syntaxIdentityOps
import org.redisson.config.Config

package object module {

  private[redisson] def setupRedissonConfig(
    config: Config,
    codec: RCodec,
    configurator: Config => Config = identity
  ) = configurator(config).applyOption(codec.underlying)((builder, value) => builder.setCodec(value))

}
