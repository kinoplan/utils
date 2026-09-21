package io.kinoplan.utils.zio.redisson.models.cache

import org.redisson.api.options.LocalCachedMapOptions
import zio.Duration

/** Configuration of a `RedisLocalCache` instance.
  *
  * @param cacheSize
  *   Max number of entries held in the local cache. `0` means unbounded.
  * @param timeToLive
  *   Time to live of an entry in the local (in-process) cache. [[Duration.Zero]] means no local
  *   TTL. Redis itself is not affected: the underlying value is only removed from Redis via
  *   `RedisLocalCache.del`.
  * @param maxIdle
  *   Max idle time of an entry in the local cache. [[Duration.Zero]] means no idle timeout.
  * @param evictionPolicy
  *   Local cache eviction algorithm (`NONE`/`LRU`/`LFU`/`SOFT`/`WEAK`), relevant once `cacheSize`
  *   is reached. Default is `NONE`.
  * @param syncStrategy
  *   How local cache instances are kept coherent via Redis pub/sub (`INVALIDATE`/`UPDATE`/`NONE`).
  *   Default is `INVALIDATE`.
  * @param reconnectionStrategy
  *   How to avoid stale entries after a Redis reconnect (`NONE`/`CLEAR`/`LOAD`). Default is
  *   `CLEAR`.
  * @param cacheProvider
  *   Local cache implementation (`REDISSON`/`CAFFEINE`). Default is `REDISSON`.
  * @param storeCacheMiss
  *   Whether a lookup miss (key absent in Redis) is itself cached locally, to avoid repeatedly
  *   hitting Redis for a key that is known not to exist. Default is `false`.
  * @param expirationEventPolicy
  *   How to listen for Redis "expired" events so a whole-cache TTL (see `RedisLocalCache.expire`)
  *   also clears local caches when it fires
  *   (`DONT_SUBSCRIBE`/`SUBSCRIBE_WITH_KEYEVENT_PATTERN`/`SUBSCRIBE_WITH_KEYSPACE_CHANNEL`).
  *   Default is `SUBSCRIBE_WITH_KEYEVENT_PATTERN`, which pattern-subscribes to
  *   `__keyevent@*:expired` — set to `DONT_SUBSCRIBE` if `expire` is never used, to avoid that
  *   subscription, or to `SUBSCRIBE_WITH_KEYSPACE_CHANNEL` for a channel scoped to this cache's key
  *   instead of a DB-wide pattern.
  * @param useTopicPattern
  *   Whether this cache shares a single pattern-based pub/sub listener with every other
  *   `RedisLocalCache` created off the same `RedissonClient`, instead of opening its own dedicated
  *   subscription. Worth enabling when an application opens many caches, to cut down on the number
  *   of Redis subscriptions. Default is `false`.
  * @param storeMode
  *   Whether data is persisted to Redis (`LOCALCACHE_REDIS`, the default `RedisLocalCache`
  *   contract) or kept only in the local caches of connected instances, with Redis used solely as a
  *   pub/sub bus (`LOCALCACHE`). Switching to `LOCALCACHE` breaks `RedisLocalCache.exists`, `ttl`,
  *   `expire`, `persist`, `clear` and `preload`, which all assume Redis-side data exists — only
  *   change this if you know you need an ephemeral, non-persistent cache. Default is
  *   `LOCALCACHE_REDIS`.
  * @param useObjectAsCacheKey
  *   Stores the decoded key object itself in the local cache, instead of an internal hash of its
  *   encoded bytes — skips re-hashing on lookups at the cost of relying on the key type's own
  *   `equals`/`hashCode`. Only affects behavior when `cacheProvider` is `REDISSON` (Caffeine
  *   already keys by object). Default is `false`.
  */
case class RedisLocalCacheOptions(
  cacheSize: Int = 0,
  timeToLive: Duration = Duration.Zero,
  maxIdle: Duration = Duration.Zero,
  evictionPolicy: LocalCachedMapOptions.EvictionPolicy = LocalCachedMapOptions.EvictionPolicy.NONE,
  syncStrategy: LocalCachedMapOptions.SyncStrategy = LocalCachedMapOptions.SyncStrategy.INVALIDATE,
  reconnectionStrategy: LocalCachedMapOptions.ReconnectionStrategy =
    LocalCachedMapOptions.ReconnectionStrategy.CLEAR,
  cacheProvider: LocalCachedMapOptions.CacheProvider = LocalCachedMapOptions.CacheProvider.REDISSON,
  storeCacheMiss: Boolean = false,
  expirationEventPolicy: LocalCachedMapOptions.ExpirationEventPolicy =
    LocalCachedMapOptions.ExpirationEventPolicy.SUBSCRIBE_WITH_KEYEVENT_PATTERN,
  useTopicPattern: Boolean = false,
  storeMode: LocalCachedMapOptions.StoreMode = LocalCachedMapOptions.StoreMode.LOCALCACHE_REDIS,
  useObjectAsCacheKey: Boolean = false
) {

  def withCacheSize(cacheSize: Int): RedisLocalCacheOptions = copy(cacheSize = cacheSize)

  def withTimeToLive(timeToLive: Duration): RedisLocalCacheOptions = copy(timeToLive = timeToLive)

  def withMaxIdle(maxIdle: Duration): RedisLocalCacheOptions = copy(maxIdle = maxIdle)

  def withEvictionPolicy(
    evictionPolicy: LocalCachedMapOptions.EvictionPolicy
  ): RedisLocalCacheOptions = copy(evictionPolicy = evictionPolicy)

  def withSyncStrategy(syncStrategy: LocalCachedMapOptions.SyncStrategy): RedisLocalCacheOptions =
    copy(syncStrategy = syncStrategy)

  def withReconnectionStrategy(
    reconnectionStrategy: LocalCachedMapOptions.ReconnectionStrategy
  ): RedisLocalCacheOptions = copy(reconnectionStrategy = reconnectionStrategy)

  def withCacheProvider(
    cacheProvider: LocalCachedMapOptions.CacheProvider
  ): RedisLocalCacheOptions = copy(cacheProvider = cacheProvider)

  def enableStoreCacheMiss: RedisLocalCacheOptions = copy(storeCacheMiss = true)

  def disableStoreCacheMiss: RedisLocalCacheOptions = copy(storeCacheMiss = false)

  def withExpirationEventPolicy(
    expirationEventPolicy: LocalCachedMapOptions.ExpirationEventPolicy
  ): RedisLocalCacheOptions = copy(expirationEventPolicy = expirationEventPolicy)

  def enableTopicPattern: RedisLocalCacheOptions = copy(useTopicPattern = true)

  def disableTopicPattern: RedisLocalCacheOptions = copy(useTopicPattern = false)

  def withStoreMode(storeMode: LocalCachedMapOptions.StoreMode): RedisLocalCacheOptions =
    copy(storeMode = storeMode)

  def enableObjectAsCacheKey: RedisLocalCacheOptions = copy(useObjectAsCacheKey = true)

  def disableObjectAsCacheKey: RedisLocalCacheOptions = copy(useObjectAsCacheKey = false)

}

object RedisLocalCacheOptions {
  val default: RedisLocalCacheOptions = RedisLocalCacheOptions()
}
