# Utils ZIO Redisson

A comprehensive, type-safe ZIO wrapper for Redis operations using Redisson,
providing seamless integration with ZIO applications.

## Installation

Add the following lines to the `libraryDependencies` in your `build.sbt`:

```scala
"io.kinoplan" %% "utils-zio-redisson" % ${version}
"io.kinoplan" %% "utils-redisson-codec-circe" % ${version}, // (optional) for circe support
"io.kinoplan" %% "utils-redisson-codec-play-json" % ${version}, // (optional) for play-json support
"io.kinoplan" %% "utils-redisson-codec-play2-json" % version // (optional) for play2-json support
```

## Quick Start

This minimal example shows how to use `ZIO Redisson` with `Circe` as JSON serialization:

```scala
import zio._
import io.circe.generic.JsonCodec
import io.kinoplan.utils.zio.redisson.RedisClient
import io.kinoplan.utils.zio.redisson.module.RedissonSingle
import io.kinoplan.utils.redisson.codec.CirceRedisCodecs
import io.kinoplan.utils.redisson.codec.DefaultRedisCodecs._

object MainApp extends ZIOAppDefault {

  @JsonCodec
  case class User(id: String, name: String, email: String)

  object User extends CirceRedisCodecs

  val program = for {
    redis <- ZIO.service[RedisClient]
    
    // Store a User object as JSON
    user = User("1", "Alice", "alice@example.com")
    _ <- redis.set("user:1", user)
    
    // Retrieve and decode back to User
    retrieved <- redis.get("user:1").as[User]
    _ <- Console.printLine(s"Retrieved user: $retrieved")
    
    // Work with basic types using default codecs
    _ <- redis.set("counter", 42)
    counter <- redis.get("counter").as[Int]
    _ <- Console.printLine(s"Counter value: $counter")
  } yield ()

  override def run = program
    .provide(
      RedissonSingle.live().map(_.get.module)
      // Configuration will be read from provider, e.g. application.conf
    )
    .exitCode
}
```

## Configuration

ZIO Redisson provides flexible configuration options to suit different use cases and migration scenarios.

You can configure Redis connections using ZIO Config provider, programmatic or use native Redisson configuration
for full compatibility with existing Redisson setups.

### ZIO Config Provider (Recommended)

By default, ZIO Redisson uses ZIO Config provider to configure Redis connections.

The configuration supports most standard
Redisson settings from the [official documentation](https://redisson.pro/docs/configuration/):

* [Common settings](https://redisson.pro/docs/configuration/#common-settings) - configured under `redis.*`
* [Single mode](https://redisson.pro/docs/configuration/#single-mode) - configured under `redis.single.*`
* [Sentinel mode](https://redisson.pro/docs/configuration/#sentinel-mode) - configured under `redis.sentinel.*`

Some advanced parameters (like `natMapper`, `nameMapper`, `commandMapper`, etc.) can only be configured programmatically
through the module configurator.

#### Single Mode

Minimal configuration for Redis Single (only required settings):

```hocon
redis {
  single {
    host = "localhost"
    port = 6379
  }
}
```

Extended configuration example with common and single mode settings:

```hocon
redis {
  # Common settings (see RedisCommonConfig for full)
  nettyThreads = 32
  threads = 16
  connectTimeout = 10000
  timeout = 3000
  
  single {
    # Required
    host = "localhost"
    port = 6379
    
    # Single mode settings (see RedisSingleConfig for full)
    database = 0
    connectionPoolSize = 64
    connectionMinimumIdleSize = 10         # default: 1 (overridden from Redisson's default 24)
    idleConnectionTimeout = 10000
    subscriptionConnectionPoolSize = 50
    subscriptionConnectionMinimumIdleSize = 1
    
    # Optional authentication
    password = ${?REDIS_PASSWORD}
    username = ${?REDIS_USERNAME}
  }
}
```

**Note**: By default, `connectionMinimumIdleSize = 1` is set instead of Redisson's default value of 24 to optimize
resource usage.

For a complete list of available configuration options, see `RedisCommonConfig` and `RedisSingleConfig`.

#### Sentinel Mode

Minimal configuration for Redis Sentinel (only required settings):

```hocon
redis {
  sentinel {
    host = "localhost"
    port = 26379
    masterName = "mymaster"
    
    # Alternative: specify multiple sentinel addresses
    # addresses = [
    #   "redis://sentinel1:26379",
    #   "redis://sentinel2:26379",
    #   "redis://sentinel3:26379"
    # ]
  }
}
```

Extended configuration example with common and sentinel mode settings:

```hocon
redis {
  # Common settings (see RedisCommonConfig for full)
  nettyThreads = 32
  threads = 16
  connectTimeout = 10000
  timeout = 3000
  
  sentinel {
    # Required
    host = "localhost"
    port = 26379
    masterName = "mymaster"

    # Alternative: specify multiple sentinel addresses
    # addresses = [
    #   "redis://sentinel1:26379",
    #   "redis://sentinel2:26379",
    #   "redis://sentinel3:26379"
    # ]
    
    # Sentinel mode settings (see RedisSentinelConfig for full)
    database = 0
    scanInterval = 1000
    checkSentinelsList = false             # default: false (overridden from Redisson's default true)
    
    # Connection pools
    connectionPoolSize = 64
    masterConnectionMinimumIdleSize = 10   # default: 1 (overridden from Redisson's default 24)
    slaveConnectionPoolSize = 64
    slaveConnectionMinimumIdleSize = 10    # default: 1 (overridden from Redisson's default 24)
    subscriptionConnectionPoolSize = 50
    subscriptionConnectionMinimumIdleSize = 1
    
    # Timeouts (milliseconds)
    idleConnectionTimeout = 10000
    retryAttempts = 3
    
    # High availability settings
    readMode = "MASTER"                    # default: MASTER (overridden from Redisson's default SLAVE)
    subscriptionMode = "MASTER"            # default: MASTER (overridden from Redisson's default SLAVE)
    loadBalancer = "ROUND_ROBIN"
    
    # Optional authentication
    password = ${?REDIS_PASSWORD}
    username = ${?REDIS_USERNAME}
  }
}
```

**Note**: Several defaults are overridden from Redisson's original values to optimize for typical use cases:

* `checkSentinelsList = false` (instead of `true`)
* `readMode = MASTER` (instead of `SLAVE`)
* `subscriptionMode = MASTER` (instead of `SLAVE`)
* `masterConnectionMinimumIdleSize = 1` (instead of `24`)
* `slaveConnectionMinimumIdleSize = 1` (instead of `24`)

For a complete list of available configuration options, see `RedisCommonConfig` and `RedisSentinelConfig` classes.

### Programmatic Configuration

Some advanced settings can only be configured programmatically, for example:

```scala
import io.kinoplan.utils.zio.redisson.module.RedissonSingle
import org.redisson.api.{HostPortNatMapper, NameMapper}

val redisLayer = RedissonSingle.live { config =>
  config
    // NAT mapper for Docker/Kubernetes environments
    .setNatMapper(new HostPortNatMapper("external-host", 6379))
    // Custom name mapper for object names
    .setNameMapper(NameMapper.direct())
    // Custom command mapper
    .setCommandMapper(CommandMapper.direct())
}
```

### YAML Configuration

Alternatively, you can use native Redisson configuration through `RedissonNative` module.

This approach allows you to use **YAML** files with the same format as native
[Redisson configuration](https://redisson.pro/docs/configuration/#using-yaml), for example:

```yaml
# single-node.yml
singleServerConfig:
  address: "redis://localhost:6379"
  database: 0
  connectionPoolSize: 64
  connectionMinimumIdleSize: 10
  timeout: 3000
  connectTimeout: 10000
```

```scala
import scala.io.Source
import io.kinoplan.utils.zio.redisson.module.RedissonNative
import org.redisson.config.Config

// Load from YAML file
val resource = Source.fromResource("single-node.yaml")
val config = Config.fromYAML(resource.reader())

val redisLayer = RedissonNative.live(config).map(_.get.module)
```

## Modules

ZIO Redisson integrates with your application through dedicated modules that create ZIO layers for dependency injection.
These modules handle Redis client lifecycle, configuration validation, and provide health monitoring capabilities.

### RedissonSingle

A module for connecting to a Single Redis server instance.

#### Basic Usage

```scala
import io.kinoplan.utils.zio.redisson.module.RedissonSingle

// Using ZIO Config Provider
val redisLive = RedissonSingle.live().map(_.get.module)
```

#### Advanced Usage

```scala
// With hybrid ZIO Config Provider & programmatic configuration
val redisLive = RedissonSingle.live { config =>
  config
    // Custom name mapper for object names
    .setNameMapper(NameMapper.direct())
    // Custom command mapper
    .setCommandMapper(CommandMapper.direct())
}.map(_.get.module)

// Using certain operations
val redissonClientLive = RedissonSingle.redissonLive()
val redisStrongOperationsLive = redissonClientLive >>> RedisStringOperations.live
```

### RedissonSentinel

A module for connecting to a Sentinel Redis server instances.

#### Basic Usage

```scala
import io.kinoplan.utils.zio.redisson.module.RedissonSentinel

// Using ZIO Config Provider
val redisLive = RedissonSentinel.live().map(_.get.module)
```

#### Advanced Usage

```scala
// With hybrid ZIO Config Provider & programmatic configuration
val redisLive = RedissonSentinel.live { config =>
  config
    // Custom name mapper for object names
    .setNameMapper(NameMapper.direct())
    // Custom command mapper
    .setCommandMapper(CommandMapper.direct())
}.map(_.get.module)

// Using certain operations
val redissonClientLive = RedissonSentinel.redissonLive()
val redisStrongOperationsLive = redissonClientLive >>> RedisStringOperations.live
```

### RedissonNative

A module for connecting to Redis server instances using native configuration.

#### Basic Usage

```scala
import io.kinoplan.utils.zio.redisson.module.RedissonNative
import org.redisson.config.Config

// Programmatic configuration
val config = new Config()
config.useSingleServer().setAddress("redis://localhost:6379")
// or
// Load from YAML file
val resource = Source.fromResource("single-node.yaml")
val config = Config.fromYAML(resource.reader())

val redisLive = RedissonNative.live(config).map(_.get.module)
```

#### Advanced Usage

```scala
// Using certain operations
val redissonClientLive = RedissonNative.redissonLive(config)
val redisStrongOperationsLive = redissonClientLive >>> RedisStringOperations.live
```

## Codecs

A codec specifies how to encode and decode values of a given type to and from Redis storage format.

ZIO Redisson uses the `RCodec[K, V]` type to represent codecs, where `K` is the key type and `V` is the value type.

Codecs consist of three components:

1. **Redisson Codec**: The underlying Redisson codec that handles low-level serialization
2. **BaseRedisEncoder/BaseRedisDecoder**: Type-safe encoders and decoders that work with higher-level types
3. **RCodec**: The ZIO Redisson wrapper that combines everything together

```scala
import org.redisson.codec.KryoCodec
import io.kinoplan.utils.redisson.codec.base.*
import io.kinoplan.utils.zio.redisson.codec.RCodec
import io.kinoplan.utils.zio.redisson.RedisClient
import zio.*

import scala.util.Try

// Example: Creating a codec for a User type
case class User(name: String, age: Int)

// 1. Define how to encode/decode User to/from String
implicit val userEncoder: BaseRedisEncoder[User, String] =
  BaseRedisEncoder.create(user => s"${user.name}:${user.age}")

implicit val userDecoder: BaseRedisDecoder[String, User] = BaseRedisDecoder.create { str =>
  Try {
    val Array(name, age) = str.split(":")
    User(name, age.toInt)
  }
}

// 2. Create RCodec that uses KryoCodec for Redisson low-level serialization (optional)
// Note: By default taken from config
implicit val codec: RCodec[String, String] = RCodec.create(new KryoCodec())

// 3. Use the codec with Redis operations
val program = for {
  redis <- ZIO.service[RedisClient]
  user = User("Alice", 30)
  _ <- redis.set("user:1", user)
  retrieved <- redis.get("user:1").as[User]
} yield retrieved
```

### Default Codecs

By default, ZIO Redisson uses a dummy `RCodec` that is passed as an implicit parameter, so you don't need to specify
it explicitly. The low-level Redisson codec for this dummy codec is automatically taken from your application configuration.

Additionally, ZIO Redisson provides high-level default codecs through `DefaultRedisCodecs`.
This trait includes common encoders and decoders for standard Scala types, making it easy to work with basic data types
without manual serialization.

Import the package (or extend the `DefaultRedisCodecs` trait) to get implicit codecs for basic types:

```scala
import io.kinoplan.utils.redisson.codec.DefaultRedisCodecs.*

// These are now available implicitly:
// - String <-> String encoders/decoders
// - Int <-> String encoders/decoders  
// - Long <-> String encoders/decoders
// - Boolean <-> String encoders/decoders
// - Double <-> String encoders/decoders
// - And other common types...

// dummy RCodec is used implicitly
val program = for {
  redis <- ZIO.service[RedisClient]
  _ <- redis.set("count", 42)          // Int automatically encoded to String
  _ <- redis.set("price", 99.99)       // Double automatically encoded to String
  _ <- redis.set("active", true)       // Boolean automatically encoded to String
  
  count <- redis.get("count").as[Int]     // String automatically decoded to Int
  price <- redis.get("price").as[Double]  // String automatically decoded to Double
  active <- redis.get("active").as[Boolean] // String automatically decoded to Boolean
} yield (count, price, active)
```

### Custom Codecs

You can create your own codecs using `RCodec` with various `Redisson` low-level codec implementations:

```scala
import org.redisson.codec.SnappyCodecV2
import org.redisson.client.codec.StringCodec
import io.kinoplan.utils.zio.redisson.codec.RCodec
import io.kinoplan.utils.redisson.codec.DefaultRedisCodecs.*

// Custom codec with Snappy compression
implicit val codec: RCodec[String, String] = 
  RCodec.create(new SnappyCodecV2(StringCodec.INSTANCE))

val program = for {
  redis <- ZIO.service[RedisClient]
  // This will use Snappy compression for storage
  _ <- redis.set("mykey", "myvalue")
  value <- redis.get("mykey").as[String]
} yield value
```

### Global Codecs

You can configure a codec globally at the layer level. This codec will be used for all Redis operations by default:

```scala
import io.kinoplan.utils.zio.redisson.module.RedissonSingle

// Configure with a specific codec for all operations
implicit val codec: RCodec[String, String] =
  RCodec.create(new SnappyCodecV2(StringCodec.INSTANCE))

val redisLayer = RedissonSingle.live()
```

**Note**: If you specify a different codec for a specific operation using an explicit implicit parameter,
it will override the global codec for that particular operation:

```scala
val program = for {
  redis <- ZIO.service[RedisClient]
  
  // Uses global codec
  _ <- redis.set("key1", "value1")
  
  // Uses operation-specific codec, overriding the global one
  _ <- redis.set("key2", "value2")(RCodec.stringCodec, DefaultRedisCodecs.stringRedisEncoder)
  
  // Back to using global codec
  _ <- redis.set("key3", "value3")
} yield ()
```

### JSON Codecs

#### Circe

To use [Circe](https://github.com/circe/circe), add the following dependency to your project:

```scala
"io.kinoplan" %% "utils-redisson-codec-circe" % version
// with provided dependencies
"io.circe" %% "circe-core" % circeV
"io.circe" %% "circe-parser" % circeV
```

Next, import the package (or extend the `CirceRedisCodecs` trait):

```scala
import io.kinoplan.utils.redisson.codec._
```

Use codecs together with case class definitions:

```scala
import io.circe.generic.JsonCodec
import io.kinoplan.utils.redisson.codec.CirceRedisCodecs

@JsonCodec
case class User(id: String, name: String, email: String)

object User extends CirceRedisCodecs

val program = for {
  redis <- ZIO.service[RedisClient]
  user = User("1", "Alice", "alice@example.com")
  _ <- redis.set("user:1", user)
  retrieved <- redis.get("user:1").as[User]
} yield retrieved
```

#### Play JSON

To use [Play JSON](https://github.com/playframework/play-json) for **Play 3.x**,
add the following dependency to your project:

```scala
"io.kinoplan" %% "utils-redisson-codec-play-json" % version
// with provided dependencies
"org.playframework" %% "play-json" % playJsonV
```

For **Play 2.x** use:

```scala
"io.kinoplan" %% "utils-redisson-codec-play2-json" % version
// with provided dependencies
"com.typesafe.play" %% "play-json" % playJsonV
```

Next, import the package (or extend the `PlayJsonRedisCodecs` trait):

```scala
import io.kinoplan.utils.redisson.codec._
```

Use codecs together as follows:

```scala
import play.api.libs.json.{Json, OFormat}
import io.kinoplan.utils.redisson.codec.PlayJsonRedisCodecs

case class User(id: String, name: String, email: String)

trait UserJson extends PlayJsonRedisCodecs {
  implicit val format: OFormat[User] = Json.format[User]
}

object User extends UserJson

val program = for {
  redis <- ZIO.service[RedisClient]
  user = User("1", "Alice", "alice@example.com")
  _ <- redis.set("user:1", user)
  retrieved <- redis.get("user:1").as[User]
} yield retrieved
```
