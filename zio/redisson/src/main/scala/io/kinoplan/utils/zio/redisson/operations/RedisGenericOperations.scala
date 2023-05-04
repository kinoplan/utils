package io.kinoplan.utils.zio.redisson.operations

import java.time.Instant
import java.util.concurrent.TimeUnit

import org.redisson.api.{RExpirableAsync, RType, RedissonClient}
import org.redisson.api.options.KeysScanOptions
import zio.{Duration, Task, URLayer, ZIO, ZLayer}
import zio.stream.ZStream

import io.kinoplan.utils.cross.collection.converters._
import io.kinoplan.utils.zio.redisson.utils.JavaDecoders

/** Interface representing operations that can be performed on Redis Generic data.
  */
trait RedisGenericOperations {

  /** Copies the value stored at the source key to the destination key.
    *
    * Similar to the COPY command.
    *
    * @param source
    *   The key from which to copy.
    * @param destination
    *   The key to which data should be copied.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def copy(source: String, destination: String): Task[Boolean]

  /** Copies the value stored at the source key to the destination key, targeting a specific
    * database.
    *
    * Similar to the COPY command.
    *
    * @param source
    *   The key from which to copy.
    * @param destination
    *   The key to which data should be copied.
    * @param db
    *   The database index to target for the destination key.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def copy(source: String, destination: String, db: Int): Task[Boolean]

  /** Copies the value from the source key to the destination key, replacing the destination if it
    * exists.
    *
    * Similar to the COPY command with REPLACE option.
    *
    * @param source
    *   The key from which to copy.
    * @param destination
    *   The key to which data should be copied or replaced.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def copyReplace(source: String, destination: String): Task[Boolean]

  /** Copies the value from the source key to the destination key in a specific database, replacing
    * the destination if it exists.
    *
    * Similar to the COPY command with REPLACE option.
    *
    * @param source
    *   The key from which to copy.
    * @param destination
    *   The key to which data should be copied or replaced.
    * @param db
    *   The database index to target for the destination key.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def copyReplace(source: String, destination: String, db: Int): Task[Boolean]

  /** Deletes a key.
    *
    * Similar to the DEL command.
    *
    * @param key
    *   The key to delete.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def del(key: String): Task[Boolean]

  /** Deletes multiple keys.
    *
    * Similar to the DEL command.
    *
    * @param keys
    *   The keys to delete.
    * @return
    *   The number of keys that were deleted.
    */
  def del(keys: Seq[String]): Task[Long]

  /** Returns a serialized version of the value stored at a key.
    *
    * Similar to the DUMP command.
    *
    * @param key
    *   The key to serialize.
    * @return
    *   The serialized value as a byte array.
    */
  def dump(key: String): Task[Array[Byte]]

  /** Checks if a key exists.
    *
    * Similar to the EXISTS command.
    *
    * @param key
    *   The key to check.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def exists(key: String): Task[Boolean]

  /** Sets a timeout on a key.
    *
    * Similar to the EXPIRE command.
    *
    * @param key
    *   The key on which to set the expiration.
    * @param duration
    *   The expiration duration.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def expire(key: String, duration: Duration): Task[Boolean]

  /** Sets a timeout on a key only if it does not already have a timeout.
    *
    * Similar to the EXPIRE command with NX option.
    *
    * @param key
    *   The key on which to set the expiration.
    * @param timeout
    *   The expiration duration.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def expireNx(key: String, timeout: Duration): Task[Boolean]

  /** Sets a timeout on a key only if it already has a timeout.
    *
    * Similar to the EXPIRE command with XX option.
    *
    * @param key
    *   The key on which to set the expiration.
    * @param timeout
    *   The expiration duration.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def expireXx(key: String, timeout: Duration): Task[Boolean]

  /** Sets a timeout on a key only if the timeout is greater than the existing timeout.
    *
    * Similar to the EXPIRE command with GT option.
    *
    * @param key
    *   The key on which to set the expiration.
    * @param timeout
    *   The expiration duration.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def expireGt(key: String, timeout: Duration): Task[Boolean]

  /** Sets a timeout on a key only if the timeout is less than the existing timeout.
    *
    * Similar to the EXPIRE command with LT option.
    *
    * @param key
    *   The key on which to set the expiration.
    * @param timeout
    *   The expiration duration.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def expireLt(key: String, timeout: Duration): Task[Boolean]

  /** Sets a timeout on a key to expire at a specific time.
    *
    * Similar to the EXPIREAT command.
    *
    * @param key
    *   The key on which to set the expiration.
    * @param time
    *   The time at which the key should expire.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def expireAt(key: String, time: Instant): Task[Boolean]

  /** Sets a timeout on a key to expire at a specific time only if it does not have a timeout.
    *
    * Similar to the EXPIREAT command with NX option.
    *
    * @param key
    *   The key on which to set the expiration.
    * @param time
    *   The time at which the key should expire.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def expireAtNx(key: String, time: Instant): Task[Boolean]

  /** Sets a timeout on a key to expire at a specific time only if it already has a timeout.
    *
    * Similar to the EXPIREAT command with XX option.
    *
    * @param key
    *   The key on which to set the expiration.
    * @param time
    *   The time at which the key should expire.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def expireAtXx(key: String, time: Instant): Task[Boolean]

  /** Sets a timeout on a key to expire at a specific time only if the new timeout is greater than
    * the existing timeout.
    *
    * Similar to the EXPIREAT command with GT option.
    *
    * @param key
    *   The key on which to set the expiration.
    * @param time
    *   The time at which the key should expire.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def expireAtGt(key: String, time: Instant): Task[Boolean]

  /** Sets a timeout on a key to expire at a specific time only if the new timeout is less than the
    * existing timeout.
    *
    * Similar to the EXPIREAT command with LT option.
    *
    * @param key
    *   The key on which to set the expiration.
    * @param time
    *   The time at which the key should expire.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def expireAtLt(key: String, time: Instant): Task[Boolean]

  /** Retrieves the expiration time of a key, if it exists.
    *
    * Similar to the EXPIRETIME command.
    *
    * @param key
    *   The key for which to retrieve the expiration time.
    * @return
    *   An option containing the expiration time as an Instant, or None if no expiration is set.
    */
  def expireTime(key: String): Task[Option[Instant]]

  /** Retrieves all keys in the current database.
    *
    * Similar to the KEYS command.
    *
    * @return
    *   An iterable collection of all keys.
    */
  def keys(): Task[Iterable[String]]

  /** Retrieves keys matching the specified options.
    *
    * Similar to the KEYS command.
    *
    * @param options
    *   The scan options to use for retrieving keys.
    * @return
    *   An iterable collection of matching keys.
    */
  def keys(options: KeysScanOptions): Task[Iterable[String]]

  /** Migrates a key to a different server or database.
    *
    * Similar to the MIGRATE command.
    *
    * @param key
    *   The key to migrate.
    * @param host
    *   The target server's host name or IP address.
    * @param port
    *   The target server's port.
    * @param db
    *   The target database number.
    * @param timeout
    *   The timeout for the migration operation.
    */
  def migrate(key: String, host: String, port: Int, db: Int, timeout: Duration): Task[Unit]

  /** Moves a key to a specified database.
    *
    * Similar to the MOVE command.
    *
    * @param key
    *   The key to move.
    * @param db
    *   The target database number.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def move(key: String, db: Int): Task[Boolean]

  /** Removes the expiration time from a key.
    *
    * Similar to the PERSIST command.
    *
    * @param key
    *   The key to persist.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def persist(key: String): Task[Boolean]

  /** Retrieves a random key from the current database.
    *
    * Similar to the RANDOMKEY command.
    *
    * @return
    *   An option containing a random key, or None if the database is empty.
    */
  def randomKey(): Task[Option[String]]

  /** Renames a key to a new key.
    *
    * Similar to the RENAME command.
    *
    * @param key
    *   The current key.
    * @param newKey
    *   The new key name.
    */
  def rename(key: String, newKey: String): Task[Unit]

  /** Renames a key to a new key only if the new key does not already exist.
    *
    * Similar to the RENAMENX command.
    *
    * @param key
    *   The current key.
    * @param newKey
    *   The new key name.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def renameNx(key: String, newKey: String): Task[Boolean]

  /** Restores a key's state from a serialized version.
    *
    * Similar to the RESTORE command.
    *
    * @param key
    *   The key to restore.
    * @param state
    *   The serialized state to restore the key to.
    */
  def restore(key: String, state: Array[Byte]): Task[Unit]

  /** Restores a key's state from a serialized version with a timeout.
    *
    * Similar to the RESTORE command with a specified expiration.
    *
    * @param key
    *   The key to restore.
    * @param timeout
    *   The expiration time for the key after restoration.
    * @param state
    *   The serialized state to restore the key to.
    */
  def restore(key: String, timeout: Duration, state: Array[Byte]): Task[Unit]

  /** Restores a key's state from a serialized version, replacing the existing key if it exists.
    *
    * Similar to the RESTORE command with REPLACE option.
    *
    * @param key
    *   The key to restore.
    * @param state
    *   The serialized state to restore the key to.
    */
  def restoreAndReplace(key: String, state: Array[Byte]): Task[Unit]

  /** Restores a key's state from a serialized version with a timeout, replacing the existing key if
    * it exists.
    *
    * Similar to the RESTORE command with REPLACE option.
    *
    * @param key
    *   The key to restore.
    * @param timeout
    *   The expiration time for the key after restoration.
    * @param state
    *   The serialized state to restore the key to.
    */
  def restoreAndReplace(key: String, timeout: Duration, state: Array[Byte]): Task[Unit]

  /** Iterates over a database's keys through a stream.
    *
    * Similar to the SCAN command.
    *
    * @return
    *   A stream of keys in the database.
    */
  def scan(): ZStream[Any, Throwable, String]

  /** Iterates over a database's keys through a stream with specific scan options.
    *
    * Similar to the SCAN command with options.
    *
    * @param options
    *   The scan options to use.
    * @return
    *   A stream of keys matching the scan options.
    */
  def scan(options: KeysScanOptions): ZStream[Any, Throwable, String]

  /** Touches a key to update its last access time.
    *
    * Similar to the TOUCH command.
    *
    * @param key
    *   The key to touch.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def touch(key: String): Task[Boolean]

  /** Retrieves the time-to-live (TTL) of a key as a duration.
    *
    * Similar to the TTL command.
    *
    * @param key
    *   The key to query for TTL.
    * @return
    *   An option containing the TTL duration, or None if no expiration is set.
    */
  def ttl(key: String): Task[Option[Duration]]

  /** Retrieves the type of value stored at a key.
    *
    * Similar to the TYPE command.
    *
    * @param key
    *   The key to query.
    * @return
    *   RType representing the type of the value stored at the key.
    */
  def getType(key: String): Task[RType]

  /** Unlinks a key, effectively deleting it in a non-blocking fashion.
    *
    * Similar to the UNLINK command.
    *
    * @param key
    *   The key to unlink.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def unlink(key: String): Task[Boolean]

}

trait RedisGenericOperationsImpl extends RedisGenericOperations {
  protected val redissonClient: RedissonClient

  private def expirable(key: String): RExpirableAsync = redissonClient.getBucket(key)

  override def copy(source: String, destination: String): Task[Boolean] = ZIO
    .fromCompletionStage(expirable(source).copyAsync(destination))
    .map(Boolean.unbox)

  override def copy(source: String, destination: String, db: Int): Task[Boolean] = ZIO
    .fromCompletionStage(expirable(source).copyAsync(destination, db))
    .map(Boolean.unbox)

  override def copyReplace(source: String, destination: String): Task[Boolean] = ZIO
    .fromCompletionStage(expirable(source).copyAndReplaceAsync(destination))
    .map(Boolean.unbox)

  override def copyReplace(source: String, destination: String, db: Int): Task[Boolean] = ZIO
    .fromCompletionStage(expirable(source).copyAndReplaceAsync(destination, db))
    .map(Boolean.unbox)

  override def del(key: String): Task[Boolean] = ZIO
    .fromCompletionStage(expirable(key).deleteAsync())
    .map(Boolean.unbox)

  override def del(keys: Seq[String]): Task[Long] = ZIO
    .fromCompletionStage(redissonClient.getKeys.deleteAsync(keys: _*))
    .map(_.toLong)

  override def dump(key: String): Task[Array[Byte]] =
    ZIO.fromCompletionStage(expirable(key).dumpAsync())

  override def exists(key: String): Task[Boolean] = ZIO
    .fromCompletionStage(expirable(key).isExistsAsync)
    .map(Boolean.unbox)

  override def expire(key: String, duration: Duration): Task[Boolean] = ZIO
    .fromCompletionStage(expirable(key).expireAsync(duration))
    .map(Boolean.unbox)

  override def expireNx(key: String, timeout: Duration): Task[Boolean] = ZIO
    .fromCompletionStage(expirable(key).expireIfNotSetAsync(timeout))
    .map(Boolean.unbox)

  override def expireXx(key: String, timeout: Duration): Task[Boolean] = ZIO
    .fromCompletionStage(expirable(key).expireIfSetAsync(timeout))
    .map(Boolean.unbox)

  override def expireGt(key: String, timeout: Duration): Task[Boolean] = ZIO
    .fromCompletionStage(expirable(key).expireIfGreaterAsync(timeout))
    .map(Boolean.unbox)

  override def expireLt(key: String, timeout: Duration): Task[Boolean] = ZIO
    .fromCompletionStage(expirable(key).expireIfLessAsync(timeout))
    .map(Boolean.unbox)

  override def expireAt(key: String, time: Instant): Task[Boolean] = ZIO
    .fromCompletionStage(expirable(key).expireAsync(time))
    .map(Boolean.unbox)

  override def expireAtNx(key: String, time: Instant): Task[Boolean] = ZIO
    .fromCompletionStage(expirable(key).expireIfNotSetAsync(time))
    .map(Boolean.unbox)

  override def expireAtXx(key: String, time: Instant): Task[Boolean] = ZIO
    .fromCompletionStage(expirable(key).expireIfSetAsync(time))
    .map(Boolean.unbox)

  override def expireAtGt(key: String, time: Instant): Task[Boolean] = ZIO
    .fromCompletionStage(expirable(key).expireIfGreaterAsync(time))
    .map(Boolean.unbox)

  override def expireAtLt(key: String, time: Instant): Task[Boolean] = ZIO
    .fromCompletionStage(expirable(key).expireIfLessAsync(time))
    .map(Boolean.unbox)

  override def expireTime(key: String): Task[Option[Instant]] = ZIO
    .fromCompletionStage(expirable(key).getExpireTimeAsync)
    .map(_.toLong)
    .map(result =>
      if (result > 0) Some(Instant.ofEpochMilli(result))
      else None
    )

  override def keys(): Task[Iterable[String]] = ZIO
    .attempt(redissonClient.getKeys.getKeys())
    .map(_.asScala)

  override def keys(options: KeysScanOptions): Task[Iterable[String]] = ZIO
    .attempt(redissonClient.getKeys.getKeys(options))
    .map(_.asScala)

  override def migrate(
    key: String,
    host: String,
    port: Int,
    db: Int,
    timeout: Duration
  ): Task[Unit] = ZIO
    .fromCompletionStage(expirable(key).migrateAsync(host, port, db, timeout.toMillis))
    .unit

  override def move(key: String, db: Int): Task[Boolean] = ZIO
    .fromCompletionStage(expirable(key).moveAsync(db))
    .map(Boolean.unbox)

  override def persist(key: String): Task[Boolean] = ZIO
    .fromCompletionStage(expirable(key).clearExpireAsync())
    .map(Boolean.unbox)

  override def randomKey(): Task[Option[String]] = ZIO
    .fromCompletionStage(redissonClient.getKeys.randomKeyAsync())
    .map(JavaDecoders.fromNullableValue[String])

  override def rename(key: String, newKey: String): Task[Unit] = ZIO
    .fromCompletionStage(expirable(key).renameAsync(newKey))
    .unit

  override def renameNx(key: String, newKey: String): Task[Boolean] = ZIO
    .fromCompletionStage(expirable(key).renamenxAsync(newKey))
    .map(Boolean.unbox)

  override def restore(key: String, state: Array[Byte]): Task[Unit] = ZIO
    .fromCompletionStage(expirable(key).restoreAsync(state))
    .unit

  override def restore(key: String, timeout: Duration, state: Array[Byte]): Task[Unit] = ZIO
    .fromCompletionStage(expirable(key).restoreAsync(state, timeout.toMillis, TimeUnit.MILLISECONDS))
    .unit

  override def restoreAndReplace(key: String, state: Array[Byte]): Task[Unit] = ZIO
    .fromCompletionStage(expirable(key).restoreAndReplaceAsync(state))
    .unit

  override def restoreAndReplace(key: String, timeout: Duration, state: Array[Byte]): Task[Unit] =
    ZIO
      .fromCompletionStage(
        expirable(key).restoreAndReplaceAsync(state, timeout.toMillis, TimeUnit.MILLISECONDS)
      )
      .unit

  override def scan(): ZStream[Any, Throwable, String] =
    ZStream.fromJavaStream(redissonClient.getKeys.getKeysStream)

  override def scan(options: KeysScanOptions): ZStream[Any, Throwable, String] = ZStream
    .fromJavaStream(redissonClient.getKeys.getKeysStream(options))

  override def touch(key: String): Task[Boolean] = ZIO
    .fromCompletionStage(expirable(key).touchAsync())
    .map(Boolean.unbox)

  override def ttl(key: String): Task[Option[Duration]] = ZIO
    .fromCompletionStage(expirable(key).remainTimeToLiveAsync())
    .map(_.toLong)
    .map(result =>
      if (result > 0) Some(Duration.fromMillis(result))
      else None
    )

  override def getType(key: String): Task[RType] =
    ZIO.fromCompletionStage(redissonClient.getKeys.getTypeAsync(key))

  override def unlink(key: String): Task[Boolean] = ZIO
    .fromCompletionStage(expirable(key).unlinkAsync())
    .map(Boolean.unbox)

}

case class RedisGenericOperationsLive(redissonClient: RedissonClient)
    extends RedisGenericOperationsImpl

object RedisGenericOperations {

  val live: URLayer[RedissonClient, RedisGenericOperations] =
    ZLayer.fromFunction(RedisGenericOperationsLive.apply _)

}
