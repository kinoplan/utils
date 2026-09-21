package io.kinoplan.utils.zio.redisson.cache

import org.redisson.api.{RLocalCachedMap, RedissonClient}
import org.redisson.api.options.LocalCachedMapOptions
import zio.{Duration, RIO, Task, ZIO}

import io.kinoplan.utils.redisson.codec.base.{BaseRedisDecoder, BaseRedisEncoder}
import io.kinoplan.utils.zio.redisson.codec.RCodec
import io.kinoplan.utils.zio.redisson.models.cache.RedisLocalCacheOptions
import io.kinoplan.utils.zio.redisson.utils.IdentitySyntax.syntaxIdentityOps
import io.kinoplan.utils.zio.redisson.utils.JavaDecoders

/** A hybrid (local + remote) cache region backed by Redis.
  *
  * A `RedisLocalCache` wraps a single Redisson `RLocalCachedMap`: reads are served from an
  * in-process cache when possible, writes go to Redis, and entries are kept coherent across
  * application instances via Redis pub/sub, using the strategy configured via
  * [[RedisLocalCacheOptions]] at creation time (see [[RedisLocalCache.make]]).
  *
  * Unlike the stateless `Redis*Operations` traits, a value of this type is a stateful resource (it
  * owns an in-process cache and a Redis pub/sub subscription) meant to be created once, via
  * [[RedisLocalCache.make]], and reused for the lifetime of the application — similar in spirit to
  * `zio.cache.Cache`.
  *
  * NOTE: `RLocalCachedMap` does not support a native per-entry Redis-side TTL in the open-source
  * edition of Redisson (that requires Redisson PRO's `RLocalCachedMapCache`). Use
  * [[RedisLocalCache.del]] to evict individual entries, or [[RedisLocalCache.expire]] for a TTL on
  * the whole cache at once.
  *
  * @tparam K
  *   Type of the keys stored in Redisson.
  * @tparam V
  *   Type of the values stored in Redisson.
  */
trait RedisLocalCache[K, V] {

  /** Retrieves a value, falling back to Redis on a local miss.
    *
    * @param key
    *   The key to retrieve the value for.
    * @param decoder
    *   The decoder instance that converts `V` to `T`.
    * @tparam T
    *   Target type of the decoded value.
    * @return
    *   An Option containing the value, or None if the key does not exist.
    */
  def get[T](key: K)(implicit
    decoder: BaseRedisDecoder[V, T]
  ): Task[Option[T]]

  /** Sets the value of a key.
    *
    * @param key
    *   The key to set.
    * @param value
    *   The value to set.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Type of the value to encode.
    */
  def set[T](key: K, value: T)(implicit
    encoder: BaseRedisEncoder[T, V]
  ): Task[Unit]

  /** Retrieves a value, computing and storing it via `orElse` on a miss — the usual "cache-aside"
    * pattern in one call.
    *
    * This does not lock or dedupe concurrent lookups: if several fibers/instances miss the same key
    * at once, `orElse` may run more than once for it, with the last write winning.
    *
    * @param key
    *   The key to retrieve the value for.
    * @param orElse
    *   Computes the value when the key is absent. Only evaluated on a miss.
    * @param decoder
    *   The decoder instance that converts `V` to `T`.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Target type of the decoded value.
    * @return
    *   The cached value, or the freshly computed one on a miss.
    */
  def getOrSet[T](key: K)(orElse: => Task[T])(implicit
    decoder: BaseRedisDecoder[V, T],
    encoder: BaseRedisEncoder[T, V]
  ): Task[T]

  /** Deletes keys, propagating removal to Redis and all instances.
    *
    * @param keys
    *   Sequence of keys to delete.
    * @return
    *   The number of keys that were removed.
    */
  def del(keys: Seq[K]): Task[Long]

  /** Checks if a key exists (checking Redis, not only the local cache).
    *
    * @param key
    *   The key to check for existence.
    * @return
    *   Boolean indicating if the key exists.
    */
  def exists(key: K): Task[Boolean]

  /** Checks whether a key is currently held in the local (in-process) part of the cache, without
    * querying Redis. Useful for observability and tests of the invalidation behavior.
    *
    * @param key
    *   The key to check.
    * @return
    *   Boolean indicating if the key is present in the local cache.
    */
  def contains(key: K): Task[Boolean]

  /** Clears the local (in-process) cache across all instances via pub/sub, without touching the
    * data stored in Redis. Useful to force a refresh after an out-of-band change.
    */
  def invalidate: Task[Unit]

  /** Deletes the entire cache: removes the underlying Redis key and, atomically with that removal,
    * broadcasts a clear to every instance currently subscribed to it (including ones that are not
    * restarting), so their local caches are dropped too. Unlike [[RedisLocalCache.invalidate]],
    * this also removes the data from Redis itself.
    *
    * A good place to call this is application startup, when a deploy changes what this cache
    * represents (e.g. value schema/codec change) and old entries must not linger. Calling it on
    * every routine restart of a frequently-redeployed/autoscaled service will repeatedly empty the
    * cache for everyone sharing it, so gate it deliberately rather than running it unconditionally
    * on every boot.
    *
    * @return
    *   Boolean indicating whether the cache existed and was deleted.
    */
  def clear: Task[Boolean]

  /** Sets a TTL on the entire cache (the underlying Redis key), after which Redis deletes all
    * entries together. This is a whole-cache TTL, not a per-entry one: the open-source edition of
    * Redisson has no native per-entry TTL for local-cached maps (see [[RedisLocalCache]]).
    *
    * By default, local cache instances subscribe to Redis keyspace "expired" notifications for this
    * key and clear their own local cache when it fires, so a whole-cache expiration is propagated
    * the same way a write is. This requires the Redis server to have keyspace notifications for
    * expired events enabled (`notify-keyspace-events` including `Ex`/`g`) — without that, the
    * Redis-side data still expires normally, but local caches will not learn about it until their
    * own local `timeToLive`/`maxIdle` (see [[RedisLocalCacheOptions]]) elapses or a write happens.
    *
    * @param duration
    *   Expire time of the whole cache.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def expire(duration: Duration): Task[Boolean]

  /** Removes the TTL previously set via [[RedisLocalCache.expire]].
    *
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def persist: Task[Boolean]

  /** Retrieves the remaining TTL of the whole cache, as set via [[RedisLocalCache.expire]].
    *
    * @return
    *   An option containing the TTL, or None if it has no expiration.
    */
  def ttl: Task[Option[Duration]]

  /** Pre-warms the local cache by loading (approximately) all entries from Redis. */
  def preload: Task[Unit]

}

private[redisson] case class RedisLocalCacheLive[K, V](map: RLocalCachedMap[K, V])
    extends RedisLocalCache[K, V] {

  override def get[T](key: K)(implicit
    decoder: BaseRedisDecoder[V, T]
  ): Task[Option[T]] = ZIO
    .fromCompletionStage(map.getAsync(key))
    .map(JavaDecoders.fromNullableValue[V])
    .flatMap {
      case Some(value) => ZIO.fromTry(decoder.decode(value)).asSome
      case None        => ZIO.none
    }

  override def set[T](key: K, value: T)(implicit
    encoder: BaseRedisEncoder[T, V]
  ): Task[Unit] = ZIO.fromCompletionStage(map.fastPutAsync(key, encoder.encode(value))).unit

  override def getOrSet[T](key: K)(orElse: => Task[T])(implicit
    decoder: BaseRedisDecoder[V, T],
    encoder: BaseRedisEncoder[T, V]
  ): Task[T] = get[T](key).someOrElseZIO(orElse.tap(set(key, _)))

  override def del(keys: Seq[K]): Task[Long] = ZIO
    .fromCompletionStage(map.fastRemoveAsync(keys: _*))
    .map(_.toLong)

  override def exists(key: K): Task[Boolean] = ZIO
    .fromCompletionStage(map.containsKeyAsync(key))
    .map(Boolean.unbox)

  override def contains(key: K): Task[Boolean] = ZIO.succeed(map.cachedKeySet().contains(key))

  override def invalidate: Task[Unit] = ZIO.fromCompletionStage(map.clearLocalCacheAsync()).unit

  override def clear: Task[Boolean] = ZIO.fromCompletionStage(map.deleteAsync()).map(Boolean.unbox)

  override def expire(duration: Duration): Task[Boolean] = ZIO
    .fromCompletionStage(map.expireAsync(duration))
    .map(Boolean.unbox)

  override def persist: Task[Boolean] = ZIO
    .fromCompletionStage(map.clearExpireAsync())
    .map(Boolean.unbox)

  override def ttl: Task[Option[Duration]] = ZIO
    .fromCompletionStage(map.remainTimeToLiveAsync())
    .map(JavaDecoders.fromMillis)

  override def preload: Task[Unit] = ZIO.attemptBlocking(map.preloadCache())

}

object RedisLocalCache {

  private def buildOptions[K, V](cacheName: String, options: RedisLocalCacheOptions)(implicit
    codec: RCodec[K, V]
  ): LocalCachedMapOptions[K, V] = LocalCachedMapOptions
    .name[K, V](cacheName)
    .applyOption(codec.underlying)((builder, value) => builder.codec(value))
    .cacheSize(options.cacheSize)
    .timeToLive(options.timeToLive)
    .maxIdle(options.maxIdle)
    .evictionPolicy(options.evictionPolicy)
    .syncStrategy(options.syncStrategy)
    .reconnectionStrategy(options.reconnectionStrategy)
    .cacheProvider(options.cacheProvider)
    .storeCacheMiss(options.storeCacheMiss)
    .expirationEventPolicy(options.expirationEventPolicy)
    .useTopicPattern(options.useTopicPattern)
    .storeMode(options.storeMode)
    .useObjectAsCacheKey(options.useObjectAsCacheKey)

  /** Creates a new [[RedisLocalCache]] backed by Redis.
    *
    * Meant to be called once per cache and kept around for the lifetime of the application (e.g.
    * built into a `ZLayer` at startup), not re-created per request: each call opens a new local
    * cache instance with its own Redis pub/sub subscription.
    *
    * @param cacheName
    *   Name of the cache, backed by a Redis key of the same name.
    * @param options
    *   Configuration for the local cache.
    * @param configurator
    *   Escape hatch applied to the `LocalCachedMapOptions` built from `options`, right before the
    *   cache is created — for tweaking anything [[RedisLocalCacheOptions]] doesn't expose. Use the
    *   `make` overload taking a `LocalCachedMapOptions` directly instead if you'd rather not go
    *   through [[RedisLocalCacheOptions]] at all.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @tparam V
    *   Type of the values stored in Redisson.
    */
  def make[K, V](
    cacheName: String,
    options: RedisLocalCacheOptions = RedisLocalCacheOptions.default,
    configurator: LocalCachedMapOptions[K, V] => LocalCachedMapOptions[K, V] =
      identity[LocalCachedMapOptions[K, V]]
  )(implicit
    codec: RCodec[K, V]
  ): RIO[RedissonClient, RedisLocalCache[K, V]] = ZIO.serviceWithZIO[RedissonClient](
    redissonClient =>
      ZIO
        .attemptBlocking(
          redissonClient.getLocalCachedMap[K, V](configurator(buildOptions(cacheName, options)))
        )
        .map(RedisLocalCacheLive(_))
  )

  /** Creates a new [[RedisLocalCache]] from a fully user-built Redisson `LocalCachedMapOptions`,
    * bypassing [[RedisLocalCacheOptions]] entirely — for when you need full control (e.g. a
    * `MapLoader`/`MapWriter`, or an option this library doesn't wrap).
    *
    * @param nativeOptions
    *   Options instance, e.g. built via `LocalCachedMapOptions.name(cacheName)...`.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @tparam V
    *   Type of the values stored in Redisson.
    */
  def make[K, V](
    nativeOptions: LocalCachedMapOptions[K, V]
  ): RIO[RedissonClient, RedisLocalCache[K, V]] = ZIO.serviceWithZIO[RedissonClient](
    redissonClient =>
      ZIO
        .attemptBlocking(redissonClient.getLocalCachedMap[K, V](nativeOptions))
        .map(RedisLocalCacheLive(_))
  )

}
