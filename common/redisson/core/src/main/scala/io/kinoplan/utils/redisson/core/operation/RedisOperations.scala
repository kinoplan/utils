package io.kinoplan.utils.redisson.core.operation

trait RedisOperations
    extends RedisValueOperations
      with RedisKeysOperations
      with RedisArrayOperations
      with RedisSetOperations
      with RedisScoredSetOperations
      with RedisHashmapOperations
      with RedisTopicOperations
      with RedisStreamOperations
      with RedisServiceOperations
