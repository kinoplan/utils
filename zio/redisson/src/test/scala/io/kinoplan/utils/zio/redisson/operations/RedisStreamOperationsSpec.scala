package io.kinoplan.utils.zio.redisson.operations

import io.kinoplan.utils.redisson.codec.DefaultRedisCodecs
import io.kinoplan.utils.zio.redisson.{RedisClient, redisClient}
import io.kinoplan.utils.zio.redisson.helpers.TestSpec
import org.redisson.api.StreamMessageId
import org.redisson.api.stream._
import zio._
import zio.test._

import scala.jdk.CollectionConverters.{CollectionHasAsScala, MapHasAsScala}

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
        id1 <- redis.xAdd(init.key, "field", "Hello,")
        id2 <- redis.xAdd(init.key, "field", " World!")
        ids = NonEmptyChunk(id1, id2)
        case1 <- redis.xAck(init.key, init.group, ids)
        _ <- redis.xReadGroup[String](
          init.key,
          init.group,
          init.consumer,
          StreamReadGroupArgs.neverDelivered()
        )
        case2 <- redis.xAck(init.key, init.group, ids)
      } yield assertTrue(case1 == 0, case2 == 2)
    ),
    TestSpec(
      "xAdd",
      for {
        redis <- redisClient
        init <- initKey(redis)
        id1 <- redis.xAdd(init.key, "name", "Sara")
        result1 = Map((id1, Map(("name", "Sara"))))
        _ <- redis.xAdd(init.key, StreamMessageId.AUTO_GENERATED, "surname", "OConnor")
        id2 <- redis.xInfoStream(init.key).map(_.getLastGeneratedId)
        result2 = Map((id2, Map(("surname", "OConnor"))))
        fieldsValue3 = Map(("field1", "value1"), ("field2", "value2"), ("field3", "value3"))
        id3 <- redis.xAdd(init.key, fieldsValue3)
        result3 = Map((id3, fieldsValue3))
        fieldsValue4 = Map(("field1", "value4"), ("field2", "value5"), ("field3", "value6"))
        _ <- redis.xAdd(init.key, StreamMessageId.AUTO_GENERATED, fieldsValue4)
        id4 <- redis.xInfoStream(init.key).map(_.getLastGeneratedId)
        result4 = Map((id4, fieldsValue4))
        fieldsValues5 = Seq(
          Map(("field1", "value7"), ("field2", "value8"), ("field3", "value9")),
          Map(("field1", "value10"), ("field2", "value11"), ("field3", "value12"))
        )
        ids <- redis.xAdd(init.key, fieldsValues5)
        result5 = ids.zip(fieldsValues5).toMap
        case1 <- redis.xReadGroup[String](
          init.key,
          init.group,
          init.consumer,
          StreamReadGroupArgs.neverDelivered()
        )
        result = result1 ++ result2 ++ result3 ++ result4 ++ result5
      } yield assertTrue(case1 == result)
    ),
    TestSpec(
      "xAutoClaim",
      for {
        redis <- redisClient
        init <- initKey(redis)
        id1 <- redis.xAdd(init.key, "field", "Hello,")
        result1 = Map((id1, Map(("field", "Hello,"))))
        _ <- redis.xReadGroup[String](
          init.key,
          init.group,
          init.consumer,
          StreamReadGroupArgs.neverDelivered()
        )
        init2 <- initKey(redis)
        case1 <-
          redis.xAutoClaim(init.key, init.group, init2.consumer, Duration.Zero, StreamMessageId.ALL)
        caseResult1 = case1.getMessages.asScala.toMap.map(value => (value._1, value._2.asScala.toMap))
      } yield assertTrue(caseResult1 == result1)
    ),
    TestSpec(
      "xAutoClaimFast",
      for {
        redis <- redisClient
        init <- initKey(redis)
        id1 <- redis.xAdd(init.key, "field", "Hello,")
        result1 = List(id1)
        _ <- redis.xReadGroup[String](
          init.key,
          init.group,
          init.consumer,
          StreamReadGroupArgs.neverDelivered()
        )
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
        id1 <- redis.xAdd(init.key, "field", "Hello,")
        result1 = Map((id1, Map(("field", "Hello,"))))
        _ <- redis.xReadGroup[String](
          init.key,
          init.group,
          init.consumer,
          StreamReadGroupArgs.neverDelivered()
        )
        init2 <- initKey(redis)
        case1 <- redis.xClaim[String](init.key, init.group, init2.consumer, Duration.Zero, Seq(id1))
      } yield assertTrue(case1 == result1)
    ),
    TestSpec(
      "xClaimFast",
      for {
        redis <- redisClient
        init <- initKey(redis)
        id1 <- redis.xAdd(init.key, "field", "Hello,")
        _ <- redis.xReadGroup[String](
          init.key,
          init.group,
          init.consumer,
          StreamReadGroupArgs.neverDelivered()
        )
        init2 <- initKey(redis)
        case1 <- redis.xClaimFast(init.key, init.group, init2.consumer, Duration.Zero, Seq(id1))
      } yield assertTrue(case1 == List(id1))
    ),
    TestSpec(
      "xDel",
      for {
        redis <- redisClient
        init <- initKey(redis)
        id1 <- redis.xAdd(init.key, "a", "1")
        id2 <- redis.xAdd(init.key, "b", "2")
        id3 <- redis.xAdd(init.key, "c", "3")
        result2 = Map((id3, Map(("c", "3"))))
        case1 <- redis.xDel(init.key, Seq(id1, id2))
        case2 <- redis.xRange[String](init.key, StreamMessageId.MIN)
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
        id1 <- redis.xAdd(init.key, "a", "1")
        _ <- redis.xAdd(init.key, "b", "2")
        case1 <- redis.xReadGroup[String](
          init.key,
          init.group,
          init.consumer,
          StreamReadGroupArgs.neverDelivered()
        )
        case2 <- redis.xReadGroup[String](
          init.key,
          init.group,
          init.consumer,
          StreamReadGroupArgs.neverDelivered()
        )
        _ <- redis.xGroupSetId(init.key, init.group, id1)
        case3 <- redis.xReadGroup[String](
          init.key,
          init.group,
          init.consumer,
          StreamReadGroupArgs.neverDelivered()
        )
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
        _ <- redis.xAdd(init.key, "a", "1")
        _ <- redis.xAdd(init.key, "b", "2")
        _ <- redis.xAdd(init.key, "c", "3")
        case1 <- redis.xLen(init.key)
      } yield assertTrue(case1 == 3)
    ),
    TestSpec(
      "xPendingInfo",
      for {
        redis <- redisClient
        init <- initKey(redis)
        _ <- redis.xAdd(init.key, "field", "Hello,")
        _ <- redis.xReadGroup[String](
          init.key,
          init.group,
          init.consumer,
          StreamReadGroupArgs.neverDelivered()
        )
        case1 <- redis.xPendingInfo(init.key, init.group)
      } yield assertTrue(case1.getTotal == 1)
    ),
    TestSpec(
      "xPending",
      for {
        redis <- redisClient
        init <- initKey(redis)
        id1 <- redis.xAdd(init.key, "a", "1")
        id2 <- redis.xAdd(init.key, "b", "2")
        id3 <- redis.xAdd(init.key, "c", "3")
        ids = Set(id1, id2, id3)
        _ <- redis.xReadGroup[String](
          init.key,
          init.group,
          init.consumer,
          StreamReadGroupArgs.neverDelivered()
        )
        case1 <- redis.xPending(init.key, init.group, StreamMessageId.MIN, StreamMessageId.MAX, 10)
        case2 <- redis.xPending(
          init.key,
          init.group,
          StreamMessageId.MIN,
          StreamMessageId.MAX,
          Duration.Zero,
          10
        )
        case3 <- redis.xPending(
          init.key,
          init.group,
          init.consumer,
          StreamMessageId.MIN,
          StreamMessageId.MAX,
          10
        )
        case4 <- redis.xPending(
          init.key,
          init.group,
          init.consumer,
          StreamMessageId.MIN,
          StreamMessageId.MAX,
          Duration.Zero,
          10
        )
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
        id1 <- redis.xAdd(init.key, "a", "1")
        id2 <- redis.xAdd(init.key, "b", "2")
        _ <- redis.xAdd(init.key, "c", "3")
        case1 <- redis.xRange[String](init.key, StreamMessageId.MIN, StreamMessageId.MAX)
        case2 <- redis.xRange[String](init.key, StreamMessageId.MIN, StreamMessageId.MAX, 2)
        caseResult2 = Map((id1, Map(("a", "1"))), (id2, Map(("b", "2"))))
      } yield assertTrue(case1.size == 3, case2.size == 2, case2 == caseResult2)
    ),
    TestSpec(
      "xRead",
      for {
        redis <- redisClient
        init1 <- initKey(redis)
        init2 <- initKey(redis)
        id1 <- redis.xAdd(init1.key, "a", "1")
        id2 <- redis.xAdd(init1.key, "b", "2")
        id3 <- redis.xAdd(init2.key, "c", "3")
        case1 <- redis.xRead[String](init1.key, StreamReadArgs.greaterThan(StreamMessageId.ALL))
        case2 <- redis.xRead[String](
          init1.key,
          StreamMultiReadArgs.greaterThan(StreamMessageId.ALL, init2.key, StreamMessageId.ALL)
        )
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
        id1 <- redis.xAdd(init1.key, "a", "1")
        case1 <- redis.xReadGroup[String](init1.key, init1.group, init1.consumer)
        id2 <- redis.xAdd(init1.key, "b", "2")
        id3 <- redis.xAdd(init2.key, "c", "3")
        case2 <- redis.xReadGroup[String](
          init1.key,
          init1.group,
          init1.consumer,
          StreamMultiReadGroupArgs.greaterThan(
            StreamMessageId.NEVER_DELIVERED,
            init2.key,
            StreamMessageId.NEVER_DELIVERED
          )
        )
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
        _ <- redis.xAdd(init.key, "a", "1")
        id2 <- redis.xAdd(init.key, "b", "2")
        id3 <- redis.xAdd(init.key, "c", "3")
        case1 <- redis.xRevRange[String](init.key, StreamMessageId.MAX, StreamMessageId.MIN)
        case2 <- redis.xRevRange[String](init.key, StreamMessageId.MAX, StreamMessageId.MIN, 2)
        caseResult2 = Map((id3, Map(("c", "3"))), (id2, Map(("b", "2"))))
      } yield assertTrue(case1.size == 3, case2.size == 2, case2 == caseResult2)
    ),
    TestSpec(
      "xTrim",
      for {
        redis <- redisClient
        init <- initKey(redis)
        _ <- redis.xAdd(init.key, "a", "1")
        id2 <- redis.xAdd(init.key, "b", "2")
        _ <- redis.xAdd(init.key, "c", "3")
        case1 <- redis.xTrim(init.key, StreamTrimArgs.minId(id2).noLimit())
      } yield assertTrue(case1 == 1)
    ),
    TestSpec(
      "xTrimNonStrict",
      for {
        redis <- redisClient
        init <- initKey(redis)
        _ <- redis.xAdd(init.key, "a", "1")
        _ <- redis.xAdd(init.key, "b", "2")
        _ <- redis.xAdd(init.key, "c", "3")
        case1 <- redis.xTrimNonStrict(init.key, StreamTrimArgs.maxLen(2).limit(2))
      } yield assertTrue(case1 == 0)
    )
  )

}
