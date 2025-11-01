package io.kinoplan.utils.zio.redisson.operations

import org.redisson.api.StreamMessageId
import org.redisson.api.stream._
import zio._
import zio.test._

import io.kinoplan.utils.cross.collection.converters._
import io.kinoplan.utils.redisson.codec.DefaultRedisCodecs
import io.kinoplan.utils.zio.redisson.{RedisClient, redisClient}
import io.kinoplan.utils.zio.redisson.helpers.TestSpec
import io.kinoplan.utils.zio.redisson.models.StreamAdd

object RedisStreamOperationsSpec extends DefaultRedisCodecs {

  case class Init(key: String, group: String, consumer: String)

  private def initKey(redis: RedisClient, init: Option[Init] = None) = for {
    key <- ZIO.succeed(generateKey)
    (group, consumer) = (init.map(_.group).getOrElse(generateKey), generateKey)
    createGroupArgs = StreamCreateGroupArgs.name(group).id(StreamMessageId.ALL).makeStream()
    _ <- redis.xGroupCreate(key, createGroupArgs)
  } yield init.map(_.copy(key = key)).getOrElse(Init(key, group, consumer))

  def specs: Chunk[TestSpec[RedisClient, Throwable, TestResult]] = Chunk(
    TestSpec(
      "xAck",
      for {
        redis <- redisClient
        init <- initKey(redis)
        id1 <- redis.xAdd(init.key, StreamAdd.create("field", "Hello,"))
        id2 <- redis.xAdd(init.key, StreamAdd.create("field", " World!"))
        ids = NonEmptyChunk(id1, id2)
        case1 <- redis.xAck(init.key, init.group, ids)
        _ <- redis
          .xReadGroup(init.key, init.group, init.consumer, StreamReadGroupArgs.neverDelivered())
          .as[String]
        case2 <- redis.xAck(init.key, init.group, ids)
      } yield assertTrue(case1 == 0, case2 == 2)
    ),
    TestSpec(
      "xAdd",
      for {
        redis <- redisClient
        init <- initKey(redis)
        id1 <- redis.xAdd(init.key, StreamAdd.create("name", "Sara"))
        result1 = Map((id1, Map(("name", "Sara"))))
        _ <-
          redis.xAdd(init.key, StreamMessageId.AUTO_GENERATED, StreamAdd.create("surname", "OConnor"))
        id2 <- redis.xInfoStream(init.key).map(_.getLastGeneratedId)
        result2 = Map((id2, Map(("surname", "OConnor"))))
        fieldsValue3 = Map(("field1", "value1"), ("field2", "value2"), ("field3", "value3"))
        id3 <- redis.xAdd(init.key, StreamAdd.create(fieldsValue3))
        result3 = Map((id3, fieldsValue3))
        fieldsValue4 = Map(("field1", "value4"), ("field2", "value5"), ("field3", "value6"))
        _ <- redis.xAdd(init.key, StreamMessageId.AUTO_GENERATED, StreamAdd.create(fieldsValue4))
        id4 <- redis.xInfoStream(init.key).map(_.getLastGeneratedId)
        result4 = Map((id4, fieldsValue4))
        fieldsValues5 = Seq(
          Map(("field1", "value7"), ("field2", "value8"), ("field3", "value9")),
          Map(("field1", "value10"), ("field2", "value11"), ("field3", "value12"))
        )
        ids <- redis.xAdd(init.key, Chunk.fromIterable(fieldsValues5).map(StreamAdd.create(_)))
        result5 = ids.zip(fieldsValues5).toMap
        case1 <- redis
          .xReadGroup(init.key, init.group, init.consumer, StreamReadGroupArgs.neverDelivered())
          .as[String]
        result = result1 ++ result2 ++ result3 ++ result4 ++ result5
      } yield assertTrue(case1 == result)
    ),
    TestSpec(
      "xAutoClaim",
      for {
        redis <- redisClient
        init <- initKey(redis)
        id1 <- redis.xAdd(init.key, StreamAdd.create("field", "Hello,"))
        result1 = Map((id1, Map(("field", "Hello,"))))
        _ <- redis
          .xReadGroup(init.key, init.group, init.consumer, StreamReadGroupArgs.neverDelivered())
          .as[String]
        init2 <- initKey(redis)
        case1 <-
          redis.xAutoClaim(init.key, init.group, init2.consumer, Duration.Zero, StreamMessageId.ALL)
        caseResult1 = case1
          .getMessages
          .asScala
          .toMap
          .map { case (id, value) =>
            (id, value.asScala.toMap)
          }
      } yield assertTrue(caseResult1 == result1)
    ),
    TestSpec(
      "xAutoClaimFast",
      for {
        redis <- redisClient
        init <- initKey(redis)
        id1 <- redis.xAdd(init.key, StreamAdd.create("field", "Hello,"))
        result1 = List(id1)
        _ <- redis
          .xReadGroup(init.key, init.group, init.consumer, StreamReadGroupArgs.neverDelivered())
          .as[String]
        init2 <- initKey(redis)
        case1 <- redis.xAutoClaimFast(
          init.key,
          init.group,
          init2.consumer,
          Duration.Zero,
          StreamMessageId.ALL
        )
        caseResult1 = case1.getIds.asScala.toList
      } yield assertTrue(caseResult1 == result1)
    ),
    TestSpec(
      "xClaim",
      for {
        redis <- redisClient
        init <- initKey(redis)
        id1 <- redis.xAdd(init.key, StreamAdd.create("field", "Hello,"))
        result1 = Map((id1, Map(("field", "Hello,"))))
        _ <- redis
          .xReadGroup(init.key, init.group, init.consumer, StreamReadGroupArgs.neverDelivered())
          .as[String]
        init2 <- initKey(redis)
        case1 <-
          redis.xClaim(init.key, init.group, init2.consumer, Duration.Zero, Seq(id1)).as[String]
      } yield assertTrue(case1 == result1)
    ),
    TestSpec(
      "xClaimFast",
      for {
        redis <- redisClient
        init <- initKey(redis)
        id1 <- redis.xAdd(init.key, StreamAdd.create("field", "Hello,"))
        _ <- redis
          .xReadGroup(init.key, init.group, init.consumer, StreamReadGroupArgs.neverDelivered())
          .as[String]
        init2 <- initKey(redis)
        case1 <- redis.xClaimFast(init.key, init.group, init2.consumer, Duration.Zero, Seq(id1))
      } yield assertTrue(case1 == List(id1))
    ),
    TestSpec(
      "xDel",
      for {
        redis <- redisClient
        init <- initKey(redis)
        id1 <- redis.xAdd(init.key, StreamAdd.create("a", "1"))
        id2 <- redis.xAdd(init.key, StreamAdd.create("b", "2"))
        id3 <- redis.xAdd(init.key, StreamAdd.create("c", "3"))
        result2 = Map((id3, Map(("c", "3"))))
        case1 <- redis.xDel(init.key, Seq(id1, id2))
        case2 <- redis.xRange(init.key).as[String]
      } yield assertTrue(case1 == 2, case2 == result2)
    ),
    TestSpec(
      "xGroupCreate",
      for {
        redis <- redisClient
        (key, group) = (generateKey, generateKey)
        _ <- redis.xGroupCreate(key, StreamCreateGroupArgs.name(group).makeStream())
        case1 <- redis.xInfoGroups(key)
      } yield assertTrue(case1.exists(_.getName == group))
    ),
    TestSpec(
      "xGroupCreateConsumer",
      for {
        redis <- redisClient
        (key, group, consumer) = (generateKey, generateKey, generateKey)
        _ <- redis.xGroupCreate(key, StreamCreateGroupArgs.name(group).makeStream())
        _ <- redis.xGroupCreateConsumer(key, group, consumer)
        case1 <- redis.xInfoGroups(key)
      } yield assertTrue(case1.exists(_.getName == group))
    ),
    TestSpec(
      "xGroupDelConsumer",
      for {
        redis <- redisClient
        (key, group, consumer) = (generateKey, generateKey, generateKey)
        _ <- redis.xGroupCreate(key, StreamCreateGroupArgs.name(group).makeStream())
        _ <- redis.xGroupCreateConsumer(key, group, consumer)
        case1 <- redis.xInfoGroups(key)
        _ <- redis.xGroupDelConsumer(key, group, consumer)
        case2 <- redis.xInfoGroups(key)
      } yield assertTrue(case1.exists(_.getConsumers == 1), case2.exists(_.getConsumers == 0))
    ),
    TestSpec(
      "xGroupDestroy",
      for {
        redis <- redisClient
        (key, group) = (generateKey, generateKey)
        _ <- redis.xGroupCreate(key, StreamCreateGroupArgs.name(group).makeStream())
        case1 <- redis.xInfoGroups(key)
        _ <- redis.xGroupDestroy(key, group)
        case2 <- redis.xInfoGroups(key)
      } yield assertTrue(case1.exists(_.getName == group), !case2.exists(_.getName == group))
    ),
    TestSpec(
      "xGroupSetId",
      for {
        redis <- redisClient
        init <- initKey(redis)
        id1 <- redis.xAdd(init.key, StreamAdd.create("a", "1"))
        _ <- redis.xAdd(init.key, StreamAdd.create("b", "2"))
        case1 <- redis
          .xReadGroup(init.key, init.group, init.consumer, StreamReadGroupArgs.neverDelivered())
          .as[String]
        case2 <- redis
          .xReadGroup(init.key, init.group, init.consumer, StreamReadGroupArgs.neverDelivered())
          .as[String]
        _ <- redis.xGroupSetId(init.key, init.group, id1)
        case3 <- redis
          .xReadGroup(init.key, init.group, init.consumer, StreamReadGroupArgs.neverDelivered())
          .as[String]
      } yield assertTrue(case1.size == 2, case2.isEmpty, case3.size == 1)
    ),
    TestSpec(
      "xInfoConsumers",
      for {
        redis <- redisClient
        init <- initKey(redis)
        _ <- redis.xGroupCreateConsumer(init.key, init.group, init.consumer)
        case1 <- redis.xInfoConsumers(init.key, init.group)
      } yield assertTrue(case1.exists(_.getName == init.consumer))
    ),
    TestSpec(
      "xInfoGroups",
      for {
        redis <- redisClient
        init <- initKey(redis)
        case1 <- redis.xInfoGroups(init.key)
      } yield assertTrue(case1.exists(_.getName == init.group))
    ),
    TestSpec(
      "xInfoStream",
      for {
        redis <- redisClient
        init <- initKey(redis)
        case1 <- redis.xInfoStream(init.key)
      } yield assertTrue(case1.getGroups == 1)
    ),
    TestSpec(
      "xLen",
      for {
        redis <- redisClient
        init <- initKey(redis)
        _ <- redis.xAdd(init.key, StreamAdd.create("a", "1"))
        _ <- redis.xAdd(init.key, StreamAdd.create("b", "2"))
        _ <- redis.xAdd(init.key, StreamAdd.create("c", "3"))
        case1 <- redis.xLen(init.key)
      } yield assertTrue(case1 == 3)
    ),
    TestSpec(
      "xPendingInfo",
      for {
        redis <- redisClient
        init <- initKey(redis)
        _ <- redis.xAdd(init.key, StreamAdd.create("field", "Hello,"))
        _ <- redis
          .xReadGroup(init.key, init.group, init.consumer, StreamReadGroupArgs.neverDelivered())
          .as[String]
        case1 <- redis.xPendingInfo(init.key, init.group)
      } yield assertTrue(case1.getTotal == 1)
    ),
    TestSpec(
      "xPending",
      for {
        redis <- redisClient
        init <- initKey(redis)
        id1 <- redis.xAdd(init.key, StreamAdd.create("a", "1"))
        id2 <- redis.xAdd(init.key, StreamAdd.create("b", "2"))
        id3 <- redis.xAdd(init.key, StreamAdd.create("c", "3"))
        ids = Set(id1, id2, id3)
        _ <- redis
          .xReadGroup(init.key, init.group, init.consumer, StreamReadGroupArgs.neverDelivered())
          .as[String]
        args = StreamPendingRangeArgs
          .groupName(init.group)
          .startId(StreamMessageId.MIN)
          .endId(StreamMessageId.MAX)
          .count(10)
        case1 <- redis.xPending(init.key, args)
        args2 = args.idleTime(Duration.Zero)
        case2 <- redis.xPending(init.key, args2)
        args3 = args2.consumerName(init.consumer)
        case3 <- redis.xPending(init.key, args3)
        case4 <- redis.xPending(init.key, args3)
        resultIds = case4.map(_.getId).toSet
      } yield assertTrue(
        case1.size == 3,
        case2.size == 3,
        case3.size == 3,
        case4.size == 3,
        ids == resultIds
      )
    ),
    TestSpec(
      "xRange",
      for {
        redis <- redisClient
        init <- initKey(redis)
        id1 <- redis.xAdd(init.key, StreamAdd.create("a", "1"))
        id2 <- redis.xAdd(init.key, StreamAdd.create("b", "2"))
        _ <- redis.xAdd(init.key, StreamAdd.create("c", "3"))
        case1 <- redis.xRange(init.key).as[String]
        args = StreamRangeArgs.startId(StreamMessageId.MIN).endId(StreamMessageId.MAX).count(2)
        case2 <- redis.xRange(init.key, args).as[String]
        caseResult2 = Map((id1, Map(("a", "1"))), (id2, Map(("b", "2"))))
      } yield assertTrue(case1.size == 3, case2.size == 2, case2 == caseResult2)
    ),
    TestSpec(
      "xRead",
      for {
        redis <- redisClient
        init1 <- initKey(redis)
        init2 <- initKey(redis)
        id1 <- redis.xAdd(init1.key, StreamAdd.create("a", "1"))
        id2 <- redis.xAdd(init1.key, StreamAdd.create("b", "2"))
        id3 <- redis.xAdd(init2.key, StreamAdd.create("c", "3"))
        case1 <- redis.xRead(init1.key, StreamReadArgs.greaterThan(StreamMessageId.ALL)).as[String]
        case2 <- redis
          .xRead(
            init1.key,
            StreamMultiReadArgs.greaterThan(StreamMessageId.ALL, init2.key, StreamMessageId.ALL)
          )
          .as[String]
        caseResult1 = Map((id1, Map(("a", "1"))), (id2, Map(("b", "2"))))
        caseResult2 = Map(
          (init1.key, Map((id1, Map(("a", "1"))), (id2, Map(("b", "2"))))),
          (init2.key, Map((id3, Map(("c", "3")))))
        )
      } yield assertTrue(case1 == caseResult1, case2 == caseResult2)
    ),
    TestSpec(
      "xReadGroup",
      for {
        redis <- redisClient
        init1 <- initKey(redis)
        init2 <- initKey(redis, Some(init1))
        id1 <- redis.xAdd(init1.key, StreamAdd.create("a", "1"))
        case1 <- redis.xReadGroup(init1.key, init1.group, init1.consumer).as[String]
        id2 <- redis.xAdd(init1.key, StreamAdd.create("b", "2"))
        id3 <- redis.xAdd(init2.key, StreamAdd.create("c", "3"))
        case2 <- redis
          .xReadGroup(
            init1.key,
            init1.group,
            init1.consumer,
            StreamMultiReadGroupArgs.greaterThan(
              StreamMessageId.NEVER_DELIVERED,
              init2.key,
              StreamMessageId.NEVER_DELIVERED
            )
          )
          .as[String]
        caseResult1 = Map((id1, Map(("a", "1"))))
        caseResult2 =
          Map((init1.key, Map((id2, Map(("b", "2"))))), (init2.key, Map((id3, Map(("c", "3"))))))
      } yield assertTrue(case1 == caseResult1, case2 == caseResult2)
    ),
    TestSpec(
      "xRevRange",
      for {
        redis <- redisClient
        init <- initKey(redis)
        _ <- redis.xAdd(init.key, StreamAdd.create("a", "1"))
        id2 <- redis.xAdd(init.key, StreamAdd.create("b", "2"))
        id3 <- redis.xAdd(init.key, StreamAdd.create("c", "3"))
        case1 <- redis.xRevRange(init.key).as[String]
        args = StreamRangeArgs.startId(StreamMessageId.MAX).endId(StreamMessageId.MIN).count(2)
        case2 <- redis.xRevRange(init.key, args).as[String]
        caseResult2 = Map((id3, Map(("c", "3"))), (id2, Map(("b", "2"))))
      } yield assertTrue(case1.size == 3, case2.size == 2, case2 == caseResult2)
    ),
    TestSpec(
      "xTrim",
      for {
        redis <- redisClient
        init <- initKey(redis)
        _ <- redis.xAdd(init.key, StreamAdd.create("a", "1"))
        id2 <- redis.xAdd(init.key, StreamAdd.create("b", "2"))
        _ <- redis.xAdd(init.key, StreamAdd.create("c", "3"))
        case1 <- redis.xTrim(init.key, StreamTrimArgs.minId(id2).noLimit())
      } yield assertTrue(case1 == 1)
    ),
    TestSpec(
      "xTrimNonStrict",
      for {
        redis <- redisClient
        init <- initKey(redis)
        _ <- redis.xAdd(init.key, StreamAdd.create("a", "1"))
        _ <- redis.xAdd(init.key, StreamAdd.create("b", "2"))
        _ <- redis.xAdd(init.key, StreamAdd.create("c", "3"))
        case1 <- redis.xTrimNonStrict(init.key, StreamTrimArgs.maxLen(2).limit(2))
      } yield assertTrue(case1 == 0)
    )
  )

}
