package io.kinoplan.utils.zio.redisson.cache

import org.redisson.api.RedissonClient
import org.redisson.api.options.LocalCachedMapOptions
import zio._
import zio.test._

import io.kinoplan.utils.redisson.codec.DefaultRedisCodecs
import io.kinoplan.utils.zio.redisson.helpers.TestSpec
import io.kinoplan.utils.zio.redisson.operations.generateKey

object RedisLocalCacheSpec extends DefaultRedisCodecs {

  def specs: Chunk[TestSpec[RedissonClient, Throwable, TestResult]] = Chunk(
    TestSpec(
      "get/set roundtrip",
      for {
        cacheName <- ZIO.succeed(generateKey)
        key = generateKey
        cache <- RedisLocalCache.make[String, String](cacheName)
        case1 <- cache.get[String](key)
        _ <- cache.set(key, "hello")
        case2 <- cache.get[String](key)
      } yield assertTrue(case1.isEmpty, case2.contains("hello"))
    ),
    TestSpec(
      "del removes the key",
      for {
        cacheName <- ZIO.succeed(generateKey)
        key = generateKey
        cache <- RedisLocalCache.make[String, String](cacheName)
        _ <- cache.set(key, "hello")
        case1 <- cache.exists(key)
        case2 <- cache.del(Seq(key))
        case3 <- cache.exists(key)
      } yield assertTrue(case1, case2 == 1, !case3)
    ),
    TestSpec(
      "cachedContains reflects the local cache state",
      for {
        cacheName <- ZIO.succeed(generateKey)
        key = generateKey
        cache <- RedisLocalCache.make[String, String](cacheName)
        case1 <- cache.contains(key)
        _ <- cache.set(key, "hello")
        _ <- cache.get[String](key)
        case2 <- cache.contains(key)
      } yield assertTrue(!case1, case2)
    ),
    TestSpec(
      "pub/sub invalidates the local cache of other instances on change",
      for {
        cacheName <- ZIO.succeed(generateKey)
        key = generateKey
        cacheA <- RedisLocalCache.make[String, String](cacheName)
        cacheB <- RedisLocalCache.make[String, String](cacheName)
        _ <- cacheA.set(key, "one")
        case1 <- cacheB.get[String](key)
        case2 <- cacheB.contains(key)
        _ <- cacheA.set(key, "two")
        _ <- ZIO.sleep(2.seconds)
        case3 <- cacheB.contains(key)
        case4 <- cacheB.get[String](key)
      } yield assertTrue(case1.contains("one"), case2, !case3, case4.contains("two"))
    ),
    TestSpec(
      "expire/ttl/persist manage a whole-cache TTL",
      for {
        cacheName <- ZIO.succeed(generateKey)
        key = generateKey
        timeout = 60.seconds
        cache <- RedisLocalCache.make[String, String](cacheName)
        _ <- cache.set(key, "hello")
        case1 <- cache.ttl
        case2 <- cache.expire(timeout)
        case3 <- cache.ttl
        case4 <- cache.persist
        case5 <- cache.ttl
      } yield assertTrue(
        case1.isEmpty,
        case2,
        case3.exists(ttl => ttl <= timeout && ttl >= timeout.minusSeconds(5)),
        case4,
        case5.isEmpty
      )
    ),
    TestSpec(
      "getOrSet computes and stores on a miss, then serves the cached value",
      for {
        cacheName <- ZIO.succeed(generateKey)
        key = generateKey
        calls <- Ref.make(0)
        compute = calls.update(_ + 1).as("computed")
        case1 <- RedisLocalCache.make[String, String](cacheName).flatMap(_.getOrSet(key)(compute))
        case2 <- RedisLocalCache.make[String, String](cacheName).flatMap(_.getOrSet(key)(compute))
        case3 <- calls.get
      } yield assertTrue(case1 == "computed", case2 == "computed", case3 == 1)
    ),
    TestSpec(
      "configurator overrides the built LocalCachedMapOptions",
      for {
        cacheName <- ZIO.succeed(generateKey)
        key = generateKey
        cacheA <- RedisLocalCache.make[String, String](cacheName)
        cacheB <- RedisLocalCache.make[String, String](
          cacheName,
          configurator = (o: LocalCachedMapOptions[String, String]) =>
            o.syncStrategy(LocalCachedMapOptions.SyncStrategy.NONE)
        )
        _ <- cacheA.set(key, "one")
        case1 <- cacheB.get[String](key)
        _ <- cacheA.set(key, "two")
        _ <- ZIO.sleep(2.seconds)
        case2 <- cacheB.contains(key)
      } yield assertTrue(case1.contains("one"), case2)
    ),
    TestSpec(
      "make(nativeOptions) builds a cache from raw LocalCachedMapOptions",
      for {
        cacheName <- ZIO.succeed(generateKey)
        key = generateKey
        nativeOptions = LocalCachedMapOptions.name[String, String](cacheName)
        cache <- RedisLocalCache.make(nativeOptions)
        case1 <- cache.get[String](key)
        _ <- cache.set(key, "hello")
        case2 <- cache.get[String](key)
      } yield assertTrue(case1.isEmpty, case2.contains("hello"))
    ),
    TestSpec(
      "clear deletes the Redis-side data and clears the local cache of other instances",
      for {
        cacheName <- ZIO.succeed(generateKey)
        key = generateKey
        cacheA <- RedisLocalCache.make[String, String](cacheName)
        cacheB <- RedisLocalCache.make[String, String](cacheName)
        _ <- cacheA.set(key, "hello")
        _ <- cacheB.get[String](key)
        case1 <- cacheB.contains(key)
        case2 <- cacheA.clear
        _ <- ZIO.sleep(2.seconds)
        case3 <- cacheB.contains(key)
        case4 <- cacheA.exists(key)
      } yield assertTrue(case1, case2, !case3, !case4)
    )
  )

}
