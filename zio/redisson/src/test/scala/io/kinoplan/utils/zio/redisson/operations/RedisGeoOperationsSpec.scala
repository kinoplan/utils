package io.kinoplan.utils.zio.redisson.operations

import org.redisson.api.{GeoEntry, GeoOrder, GeoPosition, GeoUnit}
import org.redisson.api.geo.GeoSearchArgs
import zio._
import zio.test._

import io.kinoplan.utils.redisson.codec.DefaultRedisCodecs
import io.kinoplan.utils.zio.redisson.{RedisClient, redisClient}
import io.kinoplan.utils.zio.redisson.helpers.TestSpec

object RedisGeoOperationsSpec extends DefaultRedisCodecs {

  def specs: Chunk[TestSpec[RedisClient, Throwable, TestResult]] = Chunk(
    TestSpec(
      "geoAdd",
      for {
        redis <- redisClient
        key = generateKey
        case1 <- redis.geoAdd(
          key,
          Seq(
            new GeoEntry(13.361389, 38.115556, "Palermo"),
            new GeoEntry(15.087269, 37.502669, "Catania")
          )
        )
        case2 <- redis.geoDist(key, "Palermo", "Catania")
        case3 <-
          redis.geoSearch(key, GeoSearchArgs.from(15, 37).radius(100, GeoUnit.KILOMETERS)).as[String]
        case4 <-
          redis.geoSearch(key, GeoSearchArgs.from(15, 37).radius(200, GeoUnit.KILOMETERS)).as[String]
      } yield assertTrue(
        case1 == 2,
        case2 == 166274.1516,
        case3 == List("Catania"),
        case4 == List("Palermo", "Catania")
      )
    ),
    TestSpec(
      "geoAddXx",
      for {
        redis <- redisClient
        key = generateKey
        case1 <- redis.geoAdd(
          key,
          Seq(
            new GeoEntry(14.361389, 38.115556, "Palermo"),
            new GeoEntry(15.087269, 37.502669, "Catania")
          )
        )
        case2 <- redis.geoAddXx(key, 13.361389, 38.115556, "Palermo")
        case3 <- redis.geoAddXx(
          key,
          Seq(
            new GeoEntry(14.361389, 38.115556, "Palermo1"),
            new GeoEntry(15.087269, 37.502669, "Catania1")
          )
        )
        case4 <- redis.geoDist(key, "Palermo", "Catania")
      } yield assertTrue(case1 == 2, case2, case3 == 0, case4 == 166274.1516)
    ),
    TestSpec(
      "geoAddNx",
      for {
        redis <- redisClient
        key = generateKey
        case1 <- redis.geoAddNx(
          key,
          Seq(
            new GeoEntry(13.361389, 38.115556, "Palermo"),
            new GeoEntry(15.087269, 37.502669, "Catania")
          )
        )
        case2 <- redis.geoAddNx(key, 14.361389, 38.115556, "Palermo")
        case3 <- redis.geoAddNx(
          key,
          Seq(
            new GeoEntry(14.361389, 38.115556, "Palermo"),
            new GeoEntry(15.087269, 37.502669, "Catania")
          )
        )
        case4 <- redis.geoDist(key, "Palermo", "Catania")
      } yield assertTrue(case1 == 2, !case2, case3 == 0, case4 == 166274.1516)
    ),
    TestSpec(
      "geoDist",
      for {
        redis <- redisClient
        key = generateKey
        case1 <- redis.geoAdd(key, 12.3243, 38.0102, "Trapani")
        case2 <- redis.geoAdd(
          key,
          Seq(
            new GeoEntry(13.361389, 38.115556, "Palermo"),
            new GeoEntry(15.087269, 37.502669, "Catania")
          )
        )
        case3 <- redis.geoDist(key, "Palermo", "Catania")
        case4 <- redis.geoDist(key, "Palermo", "Catania", GeoUnit.KILOMETERS)
        case5 <- redis.geoDist(key, "Palermo", "Catania", GeoUnit.MILES)
        case6 <- redis.geoDist(key, "Palermo", "Trapani")
        case7 <- redis.geoDist(key, "Foo", "Bar")
      } yield assertTrue(
        case1 == 1,
        case2 == 2,
        case3 == 166274.1516,
        case4 == 166.2742,
        case5 == 103.3182,
        case6 == 91572.4697,
        case7 == 0
      )
    ),
    TestSpec(
      "geoHash",
      for {
        redis <- redisClient
        key = generateKey
        _ <- redis.geoAdd(
          key,
          Seq(
            new GeoEntry(13.361389, 38.115556, "Palermo"),
            new GeoEntry(15.087269, 37.502669, "Catania")
          )
        )
        case1 <- redis.geoHash(key, Seq("Palermo", "Catania"))
      } yield assertTrue(case1 == Map(("Palermo", "sqc8b49rny0"), ("Catania", "sqdtr74hyu0")))
    ),
    TestSpec(
      "geoPos",
      for {
        redis <- redisClient
        key = generateKey
        _ <- redis.geoAdd(
          key,
          Seq(
            new GeoEntry(13.361389, 38.115556, "Palermo"),
            new GeoEntry(15.087269, 37.502669, "Catania")
          )
        )
        case1 <- redis.geoPos(key, Seq("Palermo", "Catania", "NonExisting"))
      } yield assertTrue(
        case1 ==
          Map(
            ("Palermo", new GeoPosition(13.361389338970184, 38.1155563954963)),
            ("Catania", new GeoPosition(15.087267458438873, 37.50266842333162))
          )
      )
    ),
    TestSpec(
      "geoSearch",
      for {
        redis <- redisClient
        key = generateKey
        _ <- redis.geoAdd(
          key,
          Seq(
            new GeoEntry(13.361389, 38.115556, "Palermo"),
            new GeoEntry(15.087269, 37.502669, "Catania")
          )
        )
        _ <- redis.geoAdd(
          key,
          Seq(
            new GeoEntry(12.758489, 38.788135, "edge1"),
            new GeoEntry(17.241510, 38.788135, "edge2")
          )
        )
        case1 <- redis
          .geoSearch(
            key,
            GeoSearchArgs.from(15, 37).radius(200, GeoUnit.KILOMETERS).order(GeoOrder.ASC)
          )
          .as[String]
        case2 <- redis
          .geoSearchWithDist(
            key,
            GeoSearchArgs.from(15, 37).box(400, 400, GeoUnit.KILOMETERS).order(GeoOrder.ASC)
          )
          .as[String]
        case3 <- redis
          .geoSearchWithCoord(
            key,
            GeoSearchArgs.from(15, 37).box(400, 400, GeoUnit.KILOMETERS).order(GeoOrder.ASC)
          )
          .as[String]
      } yield assertTrue(
        case1 == List("Catania", "Palermo"),
        case2 ==
          Map(("Catania", 56.4413), ("Palermo", 190.4424), ("edge2", 279.7403), ("edge1", 279.7405)),
        case3 ==
          Map(
            ("Catania", new GeoPosition(15.087267458438873, 37.50266842333162)),
            ("Palermo", new GeoPosition(13.361389338970184, 38.1155563954963)),
            ("edge2", new GeoPosition(17.241510450839996, 38.78813451624225)),
            ("edge1", new GeoPosition(12.75848776102066, 38.78813451624225))
          )
      )
    ),
    TestSpec(
      "geoSearchStore",
      for {
        redis <- redisClient
        (key1, key2, key3) = (generateKey, generateKey, generateKey)
        _ <- redis.geoAdd(
          key1,
          Seq(
            new GeoEntry(13.361389, 38.115556, "Palermo"),
            new GeoEntry(15.087269, 37.502669, "Catania")
          )
        )
        _ <- redis.geoAdd(
          key1,
          Seq(
            new GeoEntry(12.758489, 38.788135, "edge1"),
            new GeoEntry(17.241510, 38.788135, "edge2")
          )
        )
        case1 <- redis.geoSearchStore(
          key1,
          key2,
          GeoSearchArgs.from(15, 37).box(400, 400, GeoUnit.KILOMETERS).order(GeoOrder.ASC).count(3)
        )
        case2 <- redis
          .geoSearchWithDist(
            key2,
            GeoSearchArgs.from(15, 37).box(400, 400, GeoUnit.KILOMETERS).order(GeoOrder.ASC)
          )
          .as[String]
        case3 <- redis
          .geoSearchWithCoord(
            key2,
            GeoSearchArgs.from(15, 37).box(400, 400, GeoUnit.KILOMETERS).order(GeoOrder.ASC)
          )
          .as[String]
        case4 <- redis.geoSearchStore(
          key1,
          key3,
          GeoSearchArgs.from(15, 37).box(400, 400, GeoUnit.KILOMETERS).order(GeoOrder.ASC).count(3)
        )
        case5 <- redis.zRangeWithScores(key3).as[String]
      } yield assertTrue(
        case1 == 3,
        case2 == Map(("Catania", 56.4413), ("Palermo", 190.4424), ("edge2", 279.7403)),
        case3 ==
          Map(
            ("Catania", new GeoPosition(15.087267458438873, 37.50266842333162)),
            ("Palermo", new GeoPosition(13.361389338970184, 38.1155563954963)),
            ("edge2", new GeoPosition(17.241510450839996, 38.78813451624225))
          ),
        case4 == 3,
        case5 ==
          Map(
            ("Palermo", 3.479099956230698e15),
            ("Catania", 3.479447370796909e15),
            ("edge2", 3.481342659049484e15)
          )
      )
    )
  )

}
