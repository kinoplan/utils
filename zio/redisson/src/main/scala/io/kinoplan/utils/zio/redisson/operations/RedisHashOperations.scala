package io.kinoplan.utils.zio.redisson.operations

import io.kinoplan.utils.redisson.codec.{RedisDecoder, RedisEncoder}
import io.kinoplan.utils.zio.redisson.codec.RCodec
import io.kinoplan.utils.zio.redisson.utils.{JavaDecoders, JavaEncoders}
import org.redisson.api.{RMap, RedissonClient}
import zio.stream.ZStream
import zio.{Task, URLayer, ZIO, ZLayer}

import scala.jdk.CollectionConverters.SetHasAsJava

/** Interface representing operations that can be performed on Redis hash data.
  */
trait RedisHashOperations {

  /** Deletes fields from a hash stored at the specified key.
    *
    * Similar to the HDEL command.
    *
    * @param key
    *   The key of the hash.
    * @param fields
    *   Sequence of field names to delete.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The number of fields that were removed from the hash, not including specified but
    *   non-existing fields.
    */
  def hDel(key: String, fields: Seq[String])(implicit
    codec: RCodec
  ): Task[Long]

  /** Deletes a field-value pair from a hash stored at the specified key.
    *
    * Similar to the HDEL command.
    *
    * @tparam T
    *   Type of the value, requires RedisEncoder[T].
    * @param key
    *   The key of the hash.
    * @param field
    *   The field name to delete.
    * @param value
    *   The value to check before deletion.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def hDel[T: RedisEncoder](key: String, field: Object, value: T)(implicit
    codec: RCodec
  ): Task[Boolean]

  /** Checks if a field exists in a hash stored at the specified key.
    *
    * Similar to the HEXISTS command.
    *
    * @param key
    *   The key of the hash.
    * @param field
    *   The field name to check for existence.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def hExists(key: String, field: Object)(implicit
    codec: RCodec
  ): Task[Boolean]

//  /** Sets a timeout on a field in a hash stored at the specified key.
//    *
//    * Similar to the HEXPIRE command.
//    *
//    * @param key
//    *   The key of the hash.
//    * @param timeout
//    *   The duration before the field expires.
//    * @param field
//    *   The field name to set the expiration for.
//    * @param codec
//    *   Wrapper around Redisson codec. Default: taken from config.
//    * @return
//    *   Boolean indicating if the operation was successful.
//    */
//  def hExpire(key: String, timeout: Duration, field: String)(implicit
//    codec: RCodec
//  ): Task[Boolean]
//
//  /** Sets a timeout on multiple fields in a hash stored at the specified key.
//    *
//    * Similar to the HEXPIRE command.
//    *
//    * @param key
//    *   The key of the hash.
//    * @param timeout
//    *   The duration before the fields expire.
//    * @param fields
//    *   The set of field names to set the expiration for.
//    * @param codec
//    *   Wrapper around Redisson codec. Default: taken from config.
//    * @return
//    *   The number of fields that were set to expire.
//    */
//  def hExpire(key: String, timeout: Duration, fields: Set[String])(implicit
//    codec: RCodec
//  ): Task[Int]
//
//  /** Sets a timeout on a field in a hash stored at the specified key only if the field does not
//    * have a timeout.
//    *
//    * Similar to the HEXPIRE command with the NX option.
//    *
//    * @param key
//    *   The key of the hash.
//    * @param timeout
//    *   The duration before the field expires.
//    * @param field
//    *   The field name to set the expiration for if no timeout exists.
//    * @param codec
//    *   Wrapper around Redisson codec. Default: taken from config.
//    * @return
//    *   Boolean indicating if the operation was successful.
//    */
//  def hExpireNx(key: String, timeout: Duration, field: String)(implicit
//    codec: RCodec
//  ): Task[Boolean]
//
//  /** Sets a timeout on multiple fields in a hash only if the fields do not have a timeout.
//    *
//    * Similar to the HEXPIRE command with the NX option.
//    *
//    * @param key
//    *   The key of the hash.
//    * @param timeout
//    *   The duration before the fields expire.
//    * @param fields
//    *   The set of field names to set the expiration for if no timeout exists.
//    * @param codec
//    *   Wrapper around Redisson codec. Default: taken from config.
//    * @return
//    *   The number of fields that were set to expire.
//    */
//  def hExpireNx(key: String, timeout: Duration, fields: Set[String])(implicit
//    codec: RCodec
//  ): Task[Int]
//
//  /** Sets a timeout on a field in a hash only if the timeout is greater than the existing timeout.
//    *
//    * Similar to the HEXPIRE command with the GT option.
//    *
//    * @param key
//    *   The key of the hash.
//    * @param timeout
//    *   The duration before the field expires.
//    * @param field
//    *   The field name to set the expiration for if the new timeout is greater.
//    * @param codec
//    *   Wrapper around Redisson codec. Default: taken from config.
//    * @return
//    *   Boolean indicating if the operation was successful.
//    */
//  def hExpireGt(key: String, timeout: Duration, field: String)(implicit
//    codec: RCodec
//  ): Task[Boolean]
//
//  /** Sets a timeout on multiple fields in a hash only if the timeout is greater than the existing
//    * timeout.
//    *
//    * Similar to the HEXPIRE command with the GT option.
//    *
//    * @param key
//    *   The key of the hash.
//    * @param timeout
//    *   The duration before the fields expire.
//    * @param fields
//    *   The set of field names to set the expiration for if the new timeout is greater.
//    * @param codec
//    *   Wrapper around Redisson codec. Default: taken from config.
//    * @return
//    *   The number of fields whose timeout was extended.
//    */
//  def hExpireGt(key: String, timeout: Duration, fields: Set[String])(implicit
//    codec: RCodec
//  ): Task[Int]
//
//  /** Sets a timeout on a field in a hash only if the timeout is less than the existing timeout.
//    *
//    * Similar to the HEXPIRE command with the LT option.
//    *
//    * @param key
//    *   The key of the hash.
//    * @param timeout
//    *   The duration before the field expires.
//    * @param field
//    *   The field name to set the expiration for if the new timeout is less.
//    * @param codec
//    *   Wrapper around Redisson codec. Default: taken from config.
//    * @return
//    *   Boolean indicating if the operation was successful.
//    */
//  def hExpireLt(key: String, timeout: Duration, field: String)(implicit
//    codec: RCodec
//  ): Task[Boolean]
//
//  /** Sets a timeout on multiple fields in a hash only if the timeout is less than the existing
//    * timeout.
//    *
//    * Similar to the HEXPIRE command with the LT option.
//    *
//    * @param key
//    *   The key of the hash.
//    * @param timeout
//    *   The duration before the fields expire.
//    * @param fields
//    *   The set of field names to set the expiration for if the new timeout is less.
//    * @param codec
//    *   Wrapper around Redisson codec. Default: taken from config.
//    * @return
//    *   The number of fields whose timeout was shortened.
//    */
//  def hExpireLt(key: String, timeout: Duration, fields: Set[String])(implicit
//    codec: RCodec
//  ): Task[Int]

  /** Retrieves the value associated with the specified field in a hash.
    *
    * Similar to the HGET command.
    *
    * @tparam T
    *   Type of the value to be retrieved, requires RedisDecoder[T].
    * @param key
    *   The key of the hash.
    * @param field
    *   The field name to retrieve the value for.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   An option of the value, or None if the field does not exist.
    */
  def hGet[T: RedisDecoder](key: String, field: String)(implicit
    codec: RCodec
  ): Task[Option[T]]

  /** Retrieves all the fields and values in a hash.
    *
    * Similar to the HGETALL command.
    *
    * @tparam T
    *   Type of the values to be retrieved, requires RedisDecoder[T].
    * @param key
    *   The key of the hash.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A map of field names to their respective values.
    */
  def hGetAll[T: RedisDecoder](key: String)(implicit
    codec: RCodec
  ): Task[Map[String, T]]

  /** Retrieves and deletes the value associated with the specified field in a hash.
    *
    * Similar to the HGETDEL command.
    *
    * @tparam T
    *   Type of the value to be retrieved, requires RedisDecoder[T].
    * @param key
    *   The key of the hash.
    * @param field
    *   The field name to retrieve and delete the value for.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   An option of the value that was retrieved and deleted, or None if the field did not exist.
    */
  def hGetDel[T: RedisDecoder](key: String, field: String)(implicit
    codec: RCodec
  ): Task[Option[T]]

  /** Increments the number stored at the specified field by the specified increment.
    *
    * Similar to the HINCRBY command.
    *
    * @param key
    *   The key of the hash.
    * @param field
    *   The field name to increment the value for.
    * @param increment
    *   The increment by which to increase the value.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The new value after increment.
    */
  def hIncrBy(key: String, field: String, increment: Long)(implicit
    codec: RCodec
  ): Task[Long]

  /** Increments the number stored at the specified field by the specified floating-point increment.
    *
    * Similar to the HINCRBYFLOAT command.
    *
    * @param key
    *   The key of the hash.
    * @param field
    *   The field name to increment the value for.
    * @param increment
    *   The floating-point increment by which to increase the value.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The new value after increment.
    */
  def hIncrByFloat(key: String, field: String, increment: Double)(implicit
    codec: RCodec
  ): Task[Double]

  /** Retrieves all field names in a hash.
    *
    * Similar to the HKEYS command.
    *
    * @param key
    *   The key of the hash.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A set of all field names in the hash.
    */
  def hKeys(key: String)(implicit
    codec: RCodec
  ): Task[Set[String]]

  /** Retrieves the number of fields contained in a hash.
    *
    * Similar to the HLEN command.
    *
    * @param key
    *   The key of the hash.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The number of fields in the hash.
    */
  def hLen(key: String)(implicit
    codec: RCodec
  ): Task[Int]

  /** Retrieves the values associated with the specified fields in a hash.
    *
    * Similar to the HMGET command.
    *
    * @tparam T
    *   Type of the values to be retrieved, requires RedisDecoder[T].
    * @param key
    *   The key of the hash.
    * @param fields
    *   The set of field names to retrieve the values for.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A map of field names to their respective values.
    */
  def hmGet[T: RedisDecoder](key: String, fields: Set[String])(implicit
    codec: RCodec
  ): Task[Map[String, T]]

  /** Sets multiple field-value pairs in a hash.
    *
    * Similar to the HMSET command.
    *
    * @tparam T
    *   Type of the values to be set, requires RedisEncoder[T].
    * @param key
    *   The key of the hash.
    * @param fieldValues
    *   A map of field names to values to be set.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    */
  def hmSet[T: RedisEncoder](key: String, fieldValues: Map[String, T])(implicit
    codec: RCodec
  ): Task[Unit]

//  /** Removes the timeout on a field in a hash.
//    *
//    * Similar to the HPERSIST command.
//    *
//    * @param key
//    *   The key of the hash.
//    * @param field
//    *   The field name for which to remove the timeout.
//    * @param codec
//    *   Wrapper around Redisson codec. Default: taken from config.
//    * @return
//    *   Boolean indicating if the operation was successful.
//    */
//  def hPersist(key: String, field: String)(implicit
//    codec: RCodec
//  ): Task[Boolean]
//
//  /** Removes the timeout on multiple fields in a hash.
//    *
//    * Similar to the HPERSIST command.
//    *
//    * @param key
//    *   The key of the hash.
//    * @param fields
//    *   The set of field names for which to remove the timeout.
//    * @param codec
//    *   Wrapper around Redisson codec. Default: taken from config.
//    * @return
//    *   A map indicating which fields had their timeout removed.
//    */
//  def hPersist(key: String, fields: Set[String])(implicit
//    codec: RCodec
//  ): Task[Map[String, Boolean]]

  /** Retrieves random fields from a hash.
    *
    * Similar to the HRANDFIELD command.
    *
    * @param key
    *   The key of the hash.
    * @param count
    *   The number of random fields to retrieve. Default is 1.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A set of random field names.
    */
  def hRandField(key: String, count: Int = 1)(implicit
    codec: RCodec
  ): Task[Set[String]]

  /** Retrieves random fields and their values from a hash.
    *
    * Similar to the HRANDFIELD command with WITHVALUES option.
    *
    * @tparam T
    *   Type of the values to be retrieved, requires RedisDecoder[T].
    * @param key
    *   The key of the hash.
    * @param count
    *   The number of random fields and values to retrieve. Default is 1.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A map of random field names to their values.
    */
  def hRandFieldWithValues[T: RedisDecoder](key: String, count: Int = 1)(implicit
    codec: RCodec
  ): Task[Map[String, T]]

  /** Iterates over a hash's fields and values through streaming.
    *
    * Similar to the HSCAN command.
    *
    * @tparam T
    *   Type of the values to be scanned, requires RedisDecoder[T].
    * @param key
    *   The key of the hash.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A stream of field-value pairs.
    */
  def hScan[T: RedisDecoder](key: String)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, (String, T)]

  /** Iterates over a hash's fields and values through streaming with a specific count.
    *
    * Similar to the HSCAN command.
    *
    * @tparam T
    *   Type of the values to be scanned, requires RedisDecoder[T].
    * @param key
    *   The key of the hash.
    * @param count
    *   The number of elements to return per batch of the scan.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A stream of field-value pairs.
    */
  def hScan[T: RedisDecoder](key: String, count: Int)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, (String, T)]

  /** Iterates over a hash's fields and values through streaming with a pattern.
    *
    * Similar to the HSCAN command.
    *
    * @tparam T
    *   Type of the values to be scanned, requires RedisDecoder[T].
    * @param key
    *   The key of the hash.
    * @param pattern
    *   The pattern to match field names against.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A stream of field-value pairs.
    */
  def hScan[T: RedisDecoder](key: String, pattern: String)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, (String, T)]

  /** Iterates over a hash's fields and values through streaming with a pattern and count.
    *
    * Similar to the HSCAN command.
    *
    * @tparam T
    *   Type of the values to be scanned, requires RedisDecoder[T].
    * @param key
    *   The key of the hash.
    * @param pattern
    *   The pattern to match field names against.
    * @param count
    *   The number of elements to return per batch of the scan.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A stream of field-value pairs.
    */
  def hScan[T: RedisDecoder](key: String, pattern: String, count: Int)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, (String, T)]

  /** Iterates over a hash's field names through streaming.
    *
    * Similar to the HSCAN command with NOVALUES option.
    *
    * @param key
    *   The key of the hash.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A stream of field names.
    */
  def hScanNoValues(key: String)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, String]

  /** Iterates over a hash's field names through streaming with a specific count.
    *
    * Similar to the HSCAN command with NOVALUES option.
    *
    * @param key
    *   The key of the hash.
    * @param count
    *   The number of elements to return per batch of the scan.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A stream of field names.
    */
  def hScanNoValues(key: String, count: Int)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, String]

  /** Iterates over a hash's field names through streaming with a pattern.
    *
    * Similar to the HSCAN command with NOVALUES option.
    *
    * @param key
    *   The key of the hash.
    * @param pattern
    *   The pattern to match field names against.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A stream of field names.
    */
  def hScanNoValues(key: String, pattern: String)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, String]

  /** Iterates over a hash's field names through streaming with a pattern and count.
    *
    * Similar to the HSCAN command with NOVALUES option.
    *
    * @param key
    *   The key of the hash.
    * @param pattern
    *   The pattern to match field names against.
    * @param count
    *   The number of elements to return per batch of the scan.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A stream of field names.
    */
  def hScanNoValues(key: String, pattern: String, count: Int)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, String]

  /** Iterates over a hash's values through streaming.
    *
    * Similar to the HSCAN command with NOKEYS option.
    *
    * @tparam T
    *   Type of the values to be scanned, requires RedisDecoder[T].
    * @param key
    *   The key of the hash.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A stream of values.
    */
  def hScanNoKeys[T: RedisDecoder](key: String)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, T]

  /** Iterates over a hash's values through streaming with a specific count.
    *
    * Similar to the HSCAN command with NOKEYS option.
    *
    * @tparam T
    *   Type of the values to be scanned, requires RedisDecoder[T].
    * @param key
    *   The key of the hash.
    * @param count
    *   The number of elements to return per batch of the scan.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A stream of values.
    */
  def hScanNoKeys[T: RedisDecoder](key: String, count: Int)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, T]

  /** Iterates over a hash's values through streaming with a pattern.
    *
    * Similar to the HSCAN command with NOKEYS option.
    *
    * @tparam T
    *   Type of the values to be scanned, requires RedisDecoder[T].
    * @param key
    *   The key of the hash.
    * @param pattern
    *   The pattern to match values against.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A stream of values.
    */
  def hScanNoKeys[T: RedisDecoder](key: String, pattern: String)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, T]

  /** Iterates over a hash's values through streaming with a pattern and count.
    *
    * Similar to the HSCAN command with NOKEYS option.
    *
    * @tparam T
    *   Type of the values to be scanned, requires RedisDecoder[T].
    * @param key
    *   The key of the hash.
    * @param pattern
    *   The pattern to match values against.
    * @param count
    *   The number of elements to return per batch of the scan.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A stream of values.
    */
  def hScanNoKeys[T: RedisDecoder](key: String, pattern: String, count: Int)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, T]

  /** Sets field-value pairs in a hash.
    *
    * Similar to the HSET command.
    *
    * @tparam T
    *   Type of the values to be set, requires RedisEncoder[T].
    * @param key
    *   The key of the hash.
    * @param fieldValues
    *   A map of field names to values to be set.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    */
  def hSet[T: RedisEncoder](key: String, fieldValues: Map[String, T])(implicit
    codec: RCodec
  ): Task[Unit]

  /** Sets a field-value pair in a hash only if the field does not exist.
    *
    * Similar to the HSETNX command.
    *
    * @tparam T
    *   Type of the value to be set, requires RedisEncoder[T].
    * @param key
    *   The key of the hash.
    * @param field
    *   The field name to set.
    * @param value
    *   The value to associate with the field.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def hSetNx[T: RedisEncoder](key: String, field: String, value: T)(implicit
    codec: RCodec
  ): Task[Boolean]

  /** Gets the length of the value associated with a field in a hash.
    *
    * Similar to the HSTRLEN command.
    *
    * @param key
    *   The key of the hash.
    * @param field
    *   The field name whose value length to retrieve.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The length of the value associated with the field.
    */
  def hStrLen(key: String, field: String)(implicit
    codec: RCodec
  ): Task[Int]

//  /** Retrieves the remaining time to live of a field's timeout in a hash.
//    *
//    * Similar to the HTTL command.
//    *
//    * @param key
//    *   The key of the hash.
//    * @param field
//    *   The field name to check the TTL for.
//    * @param codec
//    *   Wrapper around Redisson codec. Default: taken from config.
//    * @return
//    *   An option containing the TTL of the field, or None if the field does not have an expiration.
//    */
//  def hTtl(key: String, field: String)(implicit
//    codec: RCodec
//  ): Task[Option[Duration]]
//
//  /** Retrieves the remaining time to live for multiple fields in a hash.
//    *
//    * Similar to the HTTL command.
//    *
//    * @param key
//    *   The key of the hash.
//    * @param fields
//    *   The set of field names to check the TTL for.
//    * @param codec
//    *   Wrapper around Redisson codec. Default: taken from config.
//    * @return
//    *   A map of fields to their respective TTL values, or None if a field does not have an
//    *   expiration.
//    */
//  def hTtl(key: String, fields: Set[String])(implicit
//    codec: RCodec
//  ): Task[Map[String, Option[Duration]]]

  /** Retrieves all values in a hash.
    *
    * Similar to the HVALS command.
    *
    * @tparam T
    *   Type of the values to be retrieved, requires RedisDecoder[T].
    * @param key
    *   The key of the hash.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   An iterable collection of all values in the hash.
    */
  def hVals[T: RedisDecoder](key: String)(implicit
    codec: RCodec
  ): Task[Iterable[T]]

}

trait RedisHashOperationsImpl extends RedisHashOperations {
  protected val redissonClient: RedissonClient

  private def map(key: String)(implicit
    codec: RCodec
  ): RMap[String, String] = codec
    .underlying
    .map(redissonClient.getMap[String, String](key, _))
    .getOrElse(redissonClient.getMap[String, String](key))

  private def mapType[T](key: String)(implicit
    codec: RCodec
  ): RMap[String, T] = codec
    .underlying
    .map(redissonClient.getMap[String, T](key, _))
    .getOrElse(redissonClient.getMap[String, T](key))

//  private def mapCacheNative(key: String)(implicit
//    codec: RCodec
//  ): RMapCacheNative[String, String] = codec
//    .underlying
//    .map(redissonClient.getMapCacheNative[String, String](key, _))
//    .getOrElse(redissonClient.getMapCacheNative[String, String](key))

  override def hDel(key: String, fields: Seq[String])(implicit
    codec: RCodec
  ): Task[Long] = ZIO.fromCompletionStage(map(key).fastRemoveAsync(fields: _*)).map(_.toLong)

  override def hDel[T: RedisEncoder](key: String, field: Object, value: T)(implicit
    codec: RCodec
  ): Task[Boolean] = ZIO
    .fromCompletionStage(map(key).removeAsync(field, RedisEncoder[T].encode(value)))
    .map(Boolean.unbox)

  override def hExists(key: String, field: Object)(implicit
    codec: RCodec
  ): Task[Boolean] = ZIO.fromCompletionStage(map(key).containsKeyAsync(field)).map(Boolean.unbox)

//  override def hExpire(key: String, timeout: Duration, field: String)(implicit
//    codec: RCodec
//  ): Task[Boolean] = ZIO
//    .fromCompletionStage(mapCacheNative(key).expireEntryAsync(field, timeout))
//    .map(Boolean.unbox)
//
//  override def hExpire(key: String, timeout: Duration, fields: Set[String])(implicit
//    codec: RCodec
//  ): Task[Int] = ZIO
//    .fromCompletionStage(mapCacheNative(key).expireEntriesAsync(fields.asJava, timeout))
//    .map(_.toInt)
//
//  override def hExpireNx(key: String, timeout: Duration, field: String)(implicit
//    codec: RCodec
//  ): Task[Boolean] = ZIO
//    .fromCompletionStage(mapCacheNative(key).expireEntryIfNotSetAsync(field, timeout))
//    .map(Boolean.unbox)
//
//  override def hExpireNx(key: String, timeout: Duration, fields: Set[String])(implicit
//    codec: RCodec
//  ): Task[Int] = ZIO
//    .fromCompletionStage(mapCacheNative(key).expireEntriesIfNotSetAsync(fields.asJava, timeout))
//    .map(_.toInt)
//
//  override def hExpireGt(key: String, timeout: Duration, field: String)(implicit
//    codec: RCodec
//  ): Task[Boolean] = ZIO
//    .fromCompletionStage(mapCacheNative(key).expireEntryIfGreaterAsync(field, timeout))
//    .map(Boolean.unbox)
//
//  override def hExpireGt(key: String, timeout: Duration, fields: Set[String])(implicit
//    codec: RCodec
//  ): Task[Int] = ZIO
//    .fromCompletionStage(mapCacheNative(key).expireEntriesIfGreaterAsync(fields.asJava, timeout))
//    .map(_.toInt)
//
//  override def hExpireLt(key: String, timeout: Duration, field: String)(implicit
//    codec: RCodec
//  ): Task[Boolean] = ZIO
//    .fromCompletionStage(mapCacheNative(key).expireEntryIfLessAsync(field, timeout))
//    .map(Boolean.unbox)
//
//  override def hExpireLt(key: String, timeout: Duration, fields: Set[String])(implicit
//    codec: RCodec
//  ): Task[Int] = ZIO
//    .fromCompletionStage(mapCacheNative(key).expireEntriesIfLessAsync(fields.asJava, timeout))
//    .map(_.toInt)

  override def hGet[T: RedisDecoder](key: String, field: String)(implicit
    codec: RCodec
  ): Task[Option[T]] = ZIO
    .fromCompletionStage(map(key).getAsync(field))
    .flatMap(JavaDecoders.fromNullableValue(_))

  override def hGetAll[T: RedisDecoder](key: String)(implicit
    codec: RCodec
  ): Task[Map[String, T]] = ZIO
    .fromCompletionStage(map(key).readAllMapAsync())
    .flatMap(JavaDecoders.fromMap(_))

  override def hGetDel[T: RedisDecoder](key: String, field: String)(implicit
    codec: RCodec
  ): Task[Option[T]] = ZIO
    .fromCompletionStage(map(key).removeAsync(field))
    .flatMap(JavaDecoders.fromNullableValue(_))

  override def hIncrBy(key: String, field: String, increment: Long)(implicit
    codec: RCodec
  ): Task[Long] = ZIO
    .fromCompletionStage(mapType[java.lang.Long](key).addAndGetAsync(field, Long.box(increment)))
    .map(_.toLong)

  override def hIncrByFloat(key: String, field: String, increment: Double)(implicit
    codec: RCodec
  ): Task[Double] = ZIO
    .fromCompletionStage(mapType[java.lang.Double](key).addAndGetAsync(field, Double.box(increment)))
    .map(_.toDouble)

  override def hKeys(key: String)(implicit
    codec: RCodec
  ): Task[Set[String]] = ZIO
    .fromCompletionStage(map(key).readAllKeySetAsync())
    .map(JavaDecoders.fromSetKeys)

  override def hLen(key: String)(implicit
    codec: RCodec
  ): Task[Int] = ZIO.fromCompletionStage(map(key).sizeAsync()).map(_.toInt)

  override def hmGet[T: RedisDecoder](key: String, fields: Set[String])(implicit
    codec: RCodec
  ): Task[Map[String, T]] = ZIO
    .fromCompletionStage(map(key).getAllAsync(fields.asJava))
    .flatMap(JavaDecoders.fromMap(_))

  override def hmSet[T: RedisEncoder](key: String, fieldValues: Map[String, T])(implicit
    codec: RCodec
  ): Task[Unit] = hSet(key, fieldValues)

//  override def hPersist(key: String, field: String)(implicit
//    codec: RCodec
//  ): Task[Boolean] = ZIO
//    .fromCompletionStage(mapCacheNative(key).clearExpireAsync(field))
//    .map(Boolean.unbox)
//
//  override def hPersist(key: String, fields: Set[String])(implicit
//    codec: RCodec
//  ): Task[Map[String, Boolean]] = ZIO
//    .fromCompletionStage(mapCacheNative(key).clearExpireAsync(fields.asJava))
//    .map(JavaDecoders.fromMapBoolean)

  override def hRandField(key: String, count: Int)(implicit
    codec: RCodec
  ): Task[Set[String]] = ZIO
    .fromCompletionStage(map(key).randomKeysAsync(count))
    .map(JavaDecoders.fromSetKeys)

  override def hRandFieldWithValues[T: RedisDecoder](key: String, count: Int)(implicit
    codec: RCodec
  ): Task[Map[String, T]] = ZIO
    .fromCompletionStage(map(key).randomEntriesAsync(count))
    .flatMap(JavaDecoders.fromMap(_))

  override def hScan[T: RedisDecoder](key: String)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, (String, T)] = ZStream
    .fromJavaStream(map(key).entrySet().stream())
    .mapZIO(JavaDecoders.fromMapEntry(_))

  override def hScan[T: RedisDecoder](key: String, count: Int)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, (String, T)] = ZStream
    .fromJavaStream(map(key).entrySet(count).stream())
    .mapZIO(JavaDecoders.fromMapEntry(_))

  override def hScan[T: RedisDecoder](key: String, pattern: String)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, (String, T)] = ZStream
    .fromJavaStream(map(key).entrySet(pattern).stream())
    .mapZIO(JavaDecoders.fromMapEntry(_))

  override def hScan[T: RedisDecoder](key: String, pattern: String, count: Int)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, (String, T)] = ZStream
    .fromJavaStream(map(key).entrySet(pattern, count).stream())
    .mapZIO(JavaDecoders.fromMapEntry(_))

  override def hScanNoValues(key: String)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, String] = ZStream.fromJavaStream(map(key).keySet().stream())

  override def hScanNoValues(key: String, count: Int)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, String] = ZStream.fromJavaStream(map(key).keySet(count).stream())

  override def hScanNoValues(key: String, pattern: String)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, String] = ZStream.fromJavaStream(map(key).keySet(pattern).stream())

  override def hScanNoValues(key: String, pattern: String, count: Int)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, String] = ZStream.fromJavaStream(map(key).keySet(pattern, count).stream())

  override def hScanNoKeys[T: RedisDecoder](key: String)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, T] = ZStream
    .fromJavaStream(map(key).values().stream())
    .mapZIO(JavaDecoders.fromValue(_))

  override def hScanNoKeys[T: RedisDecoder](key: String, count: Int)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, T] = ZStream
    .fromJavaStream(map(key).values(count).stream())
    .mapZIO(JavaDecoders.fromValue(_))

  override def hScanNoKeys[T: RedisDecoder](key: String, pattern: String)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, T] = ZStream
    .fromJavaStream(map(key).values(pattern).stream())
    .mapZIO(JavaDecoders.fromValue(_))

  override def hScanNoKeys[T: RedisDecoder](key: String, pattern: String, count: Int)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, T] = ZStream
    .fromJavaStream(map(key).values(pattern, count).stream())
    .mapZIO(JavaDecoders.fromValue(_))

  override def hSet[T: RedisEncoder](key: String, fieldValues: Map[String, T])(implicit
    codec: RCodec
  ): Task[Unit] = ZIO
    .fromCompletionStage(map(key).putAllAsync(JavaEncoders.fromMap(fieldValues)))
    .unit

  override def hSetNx[T: RedisEncoder](key: String, field: String, value: T)(implicit
    codec: RCodec
  ): Task[Boolean] = ZIO
    .fromCompletionStage(map(key).fastPutIfAbsentAsync(field, RedisEncoder[T].encode(value)))
    .map(Boolean.unbox)

  override def hStrLen(key: String, field: String)(implicit
    codec: RCodec
  ): Task[Int] = ZIO.fromCompletionStage(map(key).valueSizeAsync(field)).map(_.toInt)

//  override def hTtl(key: String, field: String)(implicit
//    codec: RCodec
//  ): Task[Option[Duration]] = ZIO
//    .fromCompletionStage(mapCacheNative(key).remainTimeToLiveAsync(field))
//    .map(JavaDecoders.fromMillis)
//
//  override def hTtl(key: String, fields: Set[String])(implicit
//    codec: RCodec
//  ): Task[Map[String, Option[Duration]]] = ZIO
//    .fromCompletionStage(mapCacheNative(key).remainTimeToLiveAsync(fields.asJava))
//    .map(JavaDecoders.fromMapMillis)

  override def hVals[T: RedisDecoder](key: String)(implicit
    codec: RCodec
  ): Task[Iterable[T]] = ZIO
    .fromCompletionStage(map(key).readAllValuesAsync())
    .flatMap(JavaDecoders.fromCollection(_))

}

case class RedisHashOperationsLive(redissonClient: RedissonClient) extends RedisHashOperationsImpl

object RedisHashOperations {

  val live: URLayer[RedissonClient, RedisHashOperations] =
    ZLayer.fromZIO(ZIO.serviceWith[RedissonClient](RedisHashOperationsLive))

}
