package io.kinoplan.utils.zio.redisson.operations

import org.redisson.api.{RMap, RMapCacheNative, RedissonClient}
import zio.{Duration, Task, URLayer, ZIO, ZLayer}
import zio.stream.ZStream

import io.kinoplan.utils.cross.collection.converters._
import io.kinoplan.utils.redisson.codec.base.{BaseRedisDecoder, BaseRedisEncoder}
import io.kinoplan.utils.zio.redisson.codec.RCodec
import io.kinoplan.utils.zio.redisson.operations.base.ResultBuilder._
import io.kinoplan.utils.zio.redisson.utils.{JavaDecoders, JavaEncoders}

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
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @return
    *   The number of fields that were removed from the hash, not including specified but
    *   non-existing fields.
    */
  def hDel[K](key: String, fields: Seq[K])(implicit
    codec: RCodec[K, _]
  ): Task[Long]

  /** Deletes a field-value pair from a hash stored at the specified key.
    *
    * Similar to the HDEL command.
    *
    * @param key
    *   The key of the hash.
    * @param field
    *   The field name to delete.
    * @param value
    *   The value to check before deletion.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def hDel[T, K, V](key: String, field: K, value: T)(implicit
    codec: RCodec[K, V],
    encoder: BaseRedisEncoder[T, V]
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
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def hExists[K](key: String, field: K)(implicit
    codec: RCodec[K, _]
  ): Task[Boolean]

  /** Sets a timeout on a field in a hash stored at the specified key.
    *
    * Similar to the HEXPIRE command.
    *
    * @param key
    *   The key of the hash.
    * @param timeout
    *   The duration before the field expires.
    * @param field
    *   The field name to set the expiration for.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def hExpire[K](key: String, timeout: Duration, field: K)(implicit
    codec: RCodec[K, _]
  ): Task[Boolean]

  /** Sets a timeout on multiple fields in a hash stored at the specified key.
    *
    * Similar to the HEXPIRE command.
    *
    * @param key
    *   The key of the hash.
    * @param timeout
    *   The duration before the fields expire.
    * @param fields
    *   The set of field names to set the expiration for.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @return
    *   The number of fields that were set to expire.
    */
  def hExpire[K](key: String, timeout: Duration, fields: Set[K])(implicit
    codec: RCodec[K, _]
  ): Task[Int]

  /** Sets a timeout on a field in a hash stored at the specified key only if the field does not
    * have a timeout.
    *
    * Similar to the HEXPIRE command with the NX option.
    *
    * @param key
    *   The key of the hash.
    * @param timeout
    *   The duration before the field expires.
    * @param field
    *   The field name to set the expiration for if no timeout exists.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def hExpireNx[K](key: String, timeout: Duration, field: K)(implicit
    codec: RCodec[K, _]
  ): Task[Boolean]

  /** Sets a timeout on multiple fields in a hash only if the fields do not have a timeout.
    *
    * Similar to the HEXPIRE command with the NX option.
    *
    * @param key
    *   The key of the hash.
    * @param timeout
    *   The duration before the fields expire.
    * @param fields
    *   The set of field names to set the expiration for if no timeout exists.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @return
    *   The number of fields that were set to expire.
    */
  def hExpireNx[K](key: String, timeout: Duration, fields: Set[K])(implicit
    codec: RCodec[K, _]
  ): Task[Int]

  /** Sets a timeout on a field in a hash only if the timeout is greater than the existing timeout.
    *
    * Similar to the HEXPIRE command with the GT option.
    *
    * @param key
    *   The key of the hash.
    * @param timeout
    *   The duration before the field expires.
    * @param field
    *   The field name to set the expiration for if the new timeout is greater.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def hExpireGt[K](key: String, timeout: Duration, field: K)(implicit
    codec: RCodec[K, _]
  ): Task[Boolean]

  /** Sets a timeout on multiple fields in a hash only if the timeout is greater than the existing
    * timeout.
    *
    * Similar to the HEXPIRE command with the GT option.
    *
    * @param key
    *   The key of the hash.
    * @param timeout
    *   The duration before the fields expire.
    * @param fields
    *   The set of field names to set the expiration for if the new timeout is greater.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @return
    *   The number of fields whose timeout was extended.
    */
  def hExpireGt[K](key: String, timeout: Duration, fields: Set[K])(implicit
    codec: RCodec[K, _]
  ): Task[Int]

  /** Sets a timeout on a field in a hash only if the timeout is less than the existing timeout.
    *
    * Similar to the HEXPIRE command with the LT option.
    *
    * @param key
    *   The key of the hash.
    * @param timeout
    *   The duration before the field expires.
    * @param field
    *   The field name to set the expiration for if the new timeout is less.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def hExpireLt[K](key: String, timeout: Duration, field: K)(implicit
    codec: RCodec[K, _]
  ): Task[Boolean]

  /** Sets a timeout on multiple fields in a hash only if the timeout is less than the existing
    * timeout.
    *
    * Similar to the HEXPIRE command with the LT option.
    *
    * @param key
    *   The key of the hash.
    * @param timeout
    *   The duration before the fields expire.
    * @param fields
    *   The set of field names to set the expiration for if the new timeout is less.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @return
    *   The number of fields whose timeout was shortened.
    */
  def hExpireLt[K](key: String, timeout: Duration, fields: Set[K])(implicit
    codec: RCodec[K, _]
  ): Task[Int]

  /** Retrieves the value associated with the specified field in a hash.
    *
    * Similar to the HGET command.
    *
    * @param key
    *   The key of the hash.
    * @param field
    *   The field name to retrieve the value for.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An option of the value, or None if the field does not exist.
    */
  def hGet[K, V](key: String, field: K)(implicit
    codec: RCodec[K, V]
  ): ResultBuilder1[V]

  /** Retrieves all the fields and values in a hash.
    *
    * Similar to the HGETALL command.
    *
    * @param key
    *   The key of the hash.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A map of field names to their respective values.
    */
  def hGetAll[K, V](key: String)(implicit
    codec: RCodec[K, V]
  ): ResultBuilder7[K, V]

  /** Retrieves and deletes the value associated with the specified field in a hash.
    *
    * Similar to the HGETDEL command.
    *
    * @param key
    *   The key of the hash.
    * @param field
    *   The field name to retrieve and delete the value for.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An option of the value that was retrieved and deleted, or None if the field did not exist.
    */
  def hGetDel[K, V](key: String, field: K)(implicit
    codec: RCodec[K, V]
  ): ResultBuilder1[V]

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
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @return
    *   The new value after increment.
    */
  def hIncrBy[K](key: String, field: K, increment: Long)(implicit
    codec: RCodec[K, _]
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
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @return
    *   The new value after increment.
    */
  def hIncrByFloat[K](key: String, field: K, increment: Double)(implicit
    codec: RCodec[K, _]
  ): Task[Double]

  /** Retrieves all field names in a hash.
    *
    * Similar to the HKEYS command.
    *
    * @param key
    *   The key of the hash.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @return
    *   A set of all field names in the hash.
    */
  def hKeys[K](key: String)(implicit
    codec: RCodec[K, _]
  ): Task[Set[K]]

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
    codec: RCodec[_, _]
  ): Task[Int]

  /** Retrieves the values associated with the specified fields in a hash.
    *
    * Similar to the HMGET command.
    *
    * @param key
    *   The key of the hash.
    * @param fields
    *   The set of field names to retrieve the values for.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A map of field names to their respective values.
    */
  def hmGet[K, V](key: String, fields: Set[K])(implicit
    codec: RCodec[K, V]
  ): ResultBuilder7[K, V]

  /** Sets multiple field-value pairs in a hash.
    *
    * Similar to the HMSET command.
    *
    * @param key
    *   The key of the hash.
    * @param fieldValues
    *   A map of field names to values to be set.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @tparam V
    *   Type of the values stored in Redisson.
    */
  def hmSet[T, K, V](key: String, fieldValues: Map[K, T])(implicit
    codec: RCodec[K, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Unit]

  /** Removes the timeout on a field in a hash.
    *
    * Similar to the HPERSIST command.
    *
    * @param key
    *   The key of the hash.
    * @param field
    *   The field name for which to remove the timeout.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def hPersist[K](key: String, field: K)(implicit
    codec: RCodec[K, _]
  ): Task[Boolean]

  /** Removes the timeout on multiple fields in a hash.
    *
    * Similar to the HPERSIST command.
    *
    * @param key
    *   The key of the hash.
    * @param fields
    *   The set of field names for which to remove the timeout.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @return
    *   A map indicating which fields had their timeout removed.
    */
  def hPersist[K](key: String, fields: Set[K])(implicit
    codec: RCodec[K, _]
  ): Task[Map[K, Boolean]]

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
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @return
    *   A set of random field names.
    */
  def hRandField[K](key: String, count: Int = 1)(implicit
    codec: RCodec[K, _]
  ): Task[Set[K]]

  /** Retrieves random fields and their values from a hash.
    *
    * Similar to the HRANDFIELD command with WITHVALUES option.
    *
    * @param key
    *   The key of the hash.
    * @param count
    *   The number of random fields and values to retrieve. Default is 1.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A map of random field names to their values.
    */
  def hRandFieldWithValues[K, V](key: String, count: Int)(implicit
    codec: RCodec[K, V]
  ): ResultBuilder7[K, V]

  /** Iterates over a hash's fields and values through streaming.
    *
    * Similar to the HSCAN command.
    *
    * @param key
    *   The key of the hash.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A stream of field-value pairs.
    */
  def hScan[K, V](key: String)(implicit
    codec: RCodec[K, V]
  ): ResultBuilder9[K, V]

  /** Iterates over a hash's fields and values through streaming with a specific count.
    *
    * Similar to the HSCAN command.
    *
    * @param key
    *   The key of the hash.
    * @param count
    *   The number of elements to return per batch of the scan.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A stream of field-value pairs.
    */
  def hScan[K, V](key: String, count: Int)(implicit
    codec: RCodec[K, V]
  ): ResultBuilder9[K, V]

  /** Iterates over a hash's fields and values through streaming with a pattern.
    *
    * Similar to the HSCAN command.
    *
    * @param key
    *   The key of the hash.
    * @param pattern
    *   The pattern to match field names against.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A stream of field-value pairs.
    */
  def hScan[K, V](key: String, pattern: String)(implicit
    codec: RCodec[K, V]
  ): ResultBuilder9[K, V]

  /** Iterates over a hash's fields and values through streaming with a pattern and count.
    *
    * Similar to the HSCAN command.
    *
    * @param key
    *   The key of the hash.
    * @param pattern
    *   The pattern to match field names against.
    * @param count
    *   The number of elements to return per batch of the scan.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A stream of field-value pairs.
    */
  def hScan[K, V](key: String, pattern: String, count: Int)(implicit
    codec: RCodec[K, V]
  ): ResultBuilder9[K, V]

  /** Iterates over a hash's field names through streaming.
    *
    * Similar to the HSCAN command with NOVALUES option.
    *
    * @param key
    *   The key of the hash.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @return
    *   A stream of field names.
    */
  def hScanNoValues[K](key: String)(implicit
    codec: RCodec[K, _]
  ): ZStream[Any, Throwable, K]

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
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @return
    *   A stream of field names.
    */
  def hScanNoValues[K](key: String, count: Int)(implicit
    codec: RCodec[K, _]
  ): ZStream[Any, Throwable, K]

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
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @return
    *   A stream of field names.
    */
  def hScanNoValues[K](key: String, pattern: String)(implicit
    codec: RCodec[K, _]
  ): ZStream[Any, Throwable, K]

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
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @return
    *   A stream of field names.
    */
  def hScanNoValues[K](key: String, pattern: String, count: Int)(implicit
    codec: RCodec[K, _]
  ): ZStream[Any, Throwable, K]

  /** Iterates over a hash's values through streaming.
    *
    * Similar to the HSCAN command with NOKEYS option.
    *
    * @param key
    *   The key of the hash.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A stream of values.
    */
  def hScanNoKeys[V](key: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder10[V]

  /** Iterates over a hash's values through streaming with a specific count.
    *
    * Similar to the HSCAN command with NOKEYS option.
    *
    * @param key
    *   The key of the hash.
    * @param count
    *   The number of elements to return per batch of the scan.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A stream of values.
    */
  def hScanNoKeys[V](key: String, count: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder10[V]

  /** Iterates over a hash's values through streaming with a pattern.
    *
    * Similar to the HSCAN command with NOKEYS option.
    *
    * @param key
    *   The key of the hash.
    * @param pattern
    *   The pattern to match values against.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A stream of values.
    */
  def hScanNoKeys[V](key: String, pattern: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder10[V]

  /** Iterates over a hash's values through streaming with a pattern and count.
    *
    * Similar to the HSCAN command with NOKEYS option.
    *
    * @param key
    *   The key of the hash.
    * @param pattern
    *   The pattern to match values against.
    * @param count
    *   The number of elements to return per batch of the scan.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A stream of values.
    */
  def hScanNoKeys[V](key: String, pattern: String, count: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder10[V]

  /** Sets field-value pairs in a hash.
    *
    * Similar to the HSET command.
    *
    * @param key
    *   The key of the hash.
    * @param fieldValues
    *   A map of field names to values to be set.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @tparam V
    *   Type of the values stored in Redisson.
    */
  def hSet[T, K, V](key: String, fieldValues: Map[K, T])(implicit
    codec: RCodec[K, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Unit]

  /** Sets a field-value pair in a hash only if the field does not exist.
    *
    * Similar to the HSETNX command.
    *
    * @param key
    *   The key of the hash.
    * @param field
    *   The field name to set.
    * @param value
    *   The value to associate with the field.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def hSetNx[T, K, V](key: String, field: K, value: T)(implicit
    codec: RCodec[K, V],
    encoder: BaseRedisEncoder[T, V]
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
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @return
    *   The length of the value associated with the field.
    */
  def hStrLen[K](key: String, field: K)(implicit
    codec: RCodec[K, _]
  ): Task[Int]

  /** Retrieves the remaining time to live of a field's timeout in a hash.
    *
    * Similar to the HTTL command.
    *
    * @param key
    *   The key of the hash.
    * @param field
    *   The field name to check the TTL for.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @return
    *   An option containing the TTL of the field, or None if the field does not have an expiration.
    */
  def hTtl[K](key: String, field: K)(implicit
    codec: RCodec[K, _]
  ): Task[Option[Duration]]

  /** Retrieves the remaining time to live for multiple fields in a hash.
    *
    * Similar to the HTTL command.
    *
    * @param key
    *   The key of the hash.
    * @param fields
    *   The set of field names to check the TTL for.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @return
    *   A map of fields to their respective TTL values, or None if a field does not have an
    *   expiration.
    */
  def hTtl[K](key: String, fields: Set[K])(implicit
    codec: RCodec[K, _]
  ): Task[Map[K, Option[Duration]]]

  /** Retrieves all values in a hash.
    *
    * Similar to the HVALS command.
    *
    * @param key
    *   The key of the hash.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An iterable collection of all values in the hash.
    */
  def hVals[V](key: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V]

}

trait RedisHashOperationsImpl extends RedisHashOperations {
  protected val redissonClient: RedissonClient

  private def map[K, V](key: String)(implicit
    codec: RCodec[K, V]
  ): RMap[K, V] = codec
    .underlying
    .map(redissonClient.getMap[K, V](key, _))
    .getOrElse(redissonClient.getMap[K, V](key))

  private def mapCacheNative[K, V](key: String)(implicit
    codec: RCodec[K, V]
  ): RMapCacheNative[K, V] = codec
    .underlying
    .map(redissonClient.getMapCacheNative[K, V](key, _))
    .getOrElse(redissonClient.getMapCacheNative[K, V](key))

  override def hDel[K](key: String, fields: Seq[K])(implicit
    codec: RCodec[K, _]
  ): Task[Long] = ZIO.fromCompletionStage(map(key).fastRemoveAsync(fields: _*)).map(_.toLong)

  override def hDel[T, K, V](key: String, field: K, value: T)(implicit
    codec: RCodec[K, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Boolean] = ZIO
    .fromCompletionStage(map(key).removeAsync(field, codec.encode(value)))
    .map(Boolean.unbox)

  override def hExists[K](key: String, field: K)(implicit
    codec: RCodec[K, _]
  ): Task[Boolean] = ZIO.fromCompletionStage(map(key).containsKeyAsync(field)).map(Boolean.unbox)

  override def hExpire[K](key: String, timeout: Duration, field: K)(implicit
    codec: RCodec[K, _]
  ): Task[Boolean] = ZIO
    .fromCompletionStage(mapCacheNative(key).expireEntryAsync(field, timeout))
    .map(Boolean.unbox)

  override def hExpire[K](key: String, timeout: Duration, fields: Set[K])(implicit
    codec: RCodec[K, _]
  ): Task[Int] = ZIO
    .fromCompletionStage(mapCacheNative(key).expireEntriesAsync(fields.asJava, timeout))
    .map(_.toInt)

  override def hExpireNx[K](key: String, timeout: Duration, field: K)(implicit
    codec: RCodec[K, _]
  ): Task[Boolean] = ZIO
    .fromCompletionStage(mapCacheNative(key).expireEntryIfNotSetAsync(field, timeout))
    .map(Boolean.unbox)

  override def hExpireNx[K](key: String, timeout: Duration, fields: Set[K])(implicit
    codec: RCodec[K, _]
  ): Task[Int] = ZIO
    .fromCompletionStage(mapCacheNative(key).expireEntriesIfNotSetAsync(fields.asJava, timeout))
    .map(_.toInt)

  override def hExpireGt[K](key: String, timeout: Duration, field: K)(implicit
    codec: RCodec[K, _]
  ): Task[Boolean] = ZIO
    .fromCompletionStage(mapCacheNative(key).expireEntryIfGreaterAsync(field, timeout))
    .map(Boolean.unbox)

  override def hExpireGt[K](key: String, timeout: Duration, fields: Set[K])(implicit
    codec: RCodec[K, _]
  ): Task[Int] = ZIO
    .fromCompletionStage(mapCacheNative(key).expireEntriesIfGreaterAsync(fields.asJava, timeout))
    .map(_.toInt)

  override def hExpireLt[K](key: String, timeout: Duration, field: K)(implicit
    codec: RCodec[K, _]
  ): Task[Boolean] = ZIO
    .fromCompletionStage(mapCacheNative(key).expireEntryIfLessAsync(field, timeout))
    .map(Boolean.unbox)

  override def hExpireLt[K](key: String, timeout: Duration, fields: Set[K])(implicit
    codec: RCodec[K, _]
  ): Task[Int] = ZIO
    .fromCompletionStage(mapCacheNative(key).expireEntriesIfLessAsync(fields.asJava, timeout))
    .map(_.toInt)

  override def hGet[K, V](key: String, field: K)(implicit
    codec: RCodec[K, V]
  ): ResultBuilder1[V] = new ResultBuilder1[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Option[T]] = ZIO
      .fromCompletionStage(map(key).getAsync(field))
      .flatMap(JavaDecoders.fromNullableValue(_))
  }

  override def hGetAll[K, V](key: String)(implicit
    codec: RCodec[K, V]
  ): ResultBuilder7[K, V] = new ResultBuilder7[K, V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Map[K, T]] = ZIO
      .fromCompletionStage(map(key).readAllMapAsync())
      .flatMap(JavaDecoders.fromMap(_))
  }

  override def hGetDel[K, V](key: String, field: K)(implicit
    codec: RCodec[K, V]
  ): ResultBuilder1[V] = new ResultBuilder1[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Option[T]] = ZIO
      .fromCompletionStage(map(key).removeAsync(field))
      .flatMap(JavaDecoders.fromNullableValue(_))
  }

  override def hIncrBy[K](key: String, field: K, increment: Long)(implicit
    codec: RCodec[K, _]
  ): Task[Long] = ZIO
    .fromCompletionStage(map(key)(codec.toLongValue).addAndGetAsync(field, Long.box(increment)))
    .map(_.toLong)

  override def hIncrByFloat[K](key: String, field: K, increment: Double)(implicit
    codec: RCodec[K, _]
  ): Task[Double] = ZIO
    .fromCompletionStage(map(key)(codec.toDoubleValue).addAndGetAsync(field, Double.box(increment)))
    .map(_.toDouble)

  override def hKeys[K](key: String)(implicit
    codec: RCodec[K, _]
  ): Task[Set[K]] = ZIO
    .fromCompletionStage(map(key).readAllKeySetAsync())
    .map(JavaDecoders.fromSetKeys)

  override def hLen(key: String)(implicit
    codec: RCodec[_, _]
  ): Task[Int] = ZIO.fromCompletionStage(map(key).sizeAsync()).map(_.toInt)

  override def hmGet[K, V](key: String, fields: Set[K])(implicit
    codec: RCodec[K, V]
  ): ResultBuilder7[K, V] = new ResultBuilder7[K, V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Map[K, T]] = ZIO
      .fromCompletionStage(map(key).getAllAsync(fields.asJava))
      .flatMap(JavaDecoders.fromMap(_))
  }

  override def hmSet[T, K, V](key: String, fieldValues: Map[K, T])(implicit
    codec: RCodec[K, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Unit] = hSet(key, fieldValues)

  override def hPersist[K](key: String, field: K)(implicit
    codec: RCodec[K, _]
  ): Task[Boolean] = ZIO
    .fromCompletionStage(mapCacheNative(key).clearExpireAsync(field))
    .map(Boolean.unbox)

  override def hPersist[K](key: String, fields: Set[K])(implicit
    codec: RCodec[K, _]
  ): Task[Map[K, Boolean]] = ZIO
    .fromCompletionStage(mapCacheNative(key).clearExpireAsync(fields.asJava))
    .map(JavaDecoders.fromMapBoolean)

  override def hRandField[K](key: String, count: Int)(implicit
    codec: RCodec[K, _]
  ): Task[Set[K]] = ZIO
    .fromCompletionStage(map(key).randomKeysAsync(count))
    .map(JavaDecoders.fromSetKeys)

  override def hRandFieldWithValues[K, V](key: String, count: Int)(implicit
    codec: RCodec[K, V]
  ): ResultBuilder7[K, V] = new ResultBuilder7[K, V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Map[K, T]] = ZIO
      .fromCompletionStage(map(key).randomEntriesAsync(count))
      .flatMap(JavaDecoders.fromMap(_))
  }

  override def hScan[K, V](key: String)(implicit
    codec: RCodec[K, V]
  ): ResultBuilder9[K, V] = new ResultBuilder9[K, V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): ZStream[Any, Throwable, (K, T)] = ZStream
      .fromJavaStream(map(key).entrySet().stream())
      .mapZIO(JavaDecoders.fromMapEntry(_))
  }

  override def hScan[K, V](key: String, count: Int)(implicit
    codec: RCodec[K, V]
  ): ResultBuilder9[K, V] = new ResultBuilder9[K, V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): ZStream[Any, Throwable, (K, T)] = ZStream
      .fromJavaStream(map(key).entrySet(count).stream())
      .mapZIO(JavaDecoders.fromMapEntry(_))
  }

  override def hScan[K, V](key: String, pattern: String)(implicit
    codec: RCodec[K, V]
  ): ResultBuilder9[K, V] = new ResultBuilder9[K, V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): ZStream[Any, Throwable, (K, T)] = ZStream
      .fromJavaStream(map(key).entrySet(pattern).stream())
      .mapZIO(JavaDecoders.fromMapEntry(_))
  }

  override def hScan[K, V](key: String, pattern: String, count: Int)(implicit
    codec: RCodec[K, V]
  ): ResultBuilder9[K, V] = new ResultBuilder9[K, V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): ZStream[Any, Throwable, (K, T)] = ZStream
      .fromJavaStream(map(key).entrySet(pattern, count).stream())
      .mapZIO(JavaDecoders.fromMapEntry(_))
  }

  override def hScanNoValues[K](key: String)(implicit
    codec: RCodec[K, _]
  ): ZStream[Any, Throwable, K] = ZStream.fromJavaStream(map(key).keySet().stream())

  override def hScanNoValues[K](key: String, count: Int)(implicit
    codec: RCodec[K, _]
  ): ZStream[Any, Throwable, K] = ZStream.fromJavaStream(map(key).keySet(count).stream())

  override def hScanNoValues[K](key: String, pattern: String)(implicit
    codec: RCodec[K, _]
  ): ZStream[Any, Throwable, K] = ZStream.fromJavaStream(map(key).keySet(pattern).stream())

  override def hScanNoValues[K](key: String, pattern: String, count: Int)(implicit
    codec: RCodec[K, _]
  ): ZStream[Any, Throwable, K] = ZStream.fromJavaStream(map(key).keySet(pattern, count).stream())

  override def hScanNoKeys[V](key: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder10[V] = new ResultBuilder10[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): ZStream[Any, Throwable, T] = ZStream
      .fromJavaStream(map(key).values().stream())
      .mapZIO(JavaDecoders.fromNoKeys(_))
  }

  override def hScanNoKeys[V](key: String, count: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder10[V] = new ResultBuilder10[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): ZStream[Any, Throwable, T] = ZStream
      .fromJavaStream(map(key).values(count).stream())
      .mapZIO(JavaDecoders.fromNoKeys(_))
  }

  override def hScanNoKeys[V](key: String, pattern: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder10[V] = new ResultBuilder10[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): ZStream[Any, Throwable, T] = ZStream
      .fromJavaStream(map(key).values(pattern).stream())
      .mapZIO(JavaDecoders.fromNoKeys(_))
  }

  override def hScanNoKeys[V](key: String, pattern: String, count: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder10[V] = new ResultBuilder10[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): ZStream[Any, Throwable, T] = ZStream
      .fromJavaStream(map(key).values(pattern, count).stream())
      .mapZIO(JavaDecoders.fromNoKeys(_))
  }

  override def hSet[T, K, V](key: String, fieldValues: Map[K, T])(implicit
    codec: RCodec[K, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Unit] = ZIO
    .fromCompletionStage(map(key).putAllAsync(JavaEncoders.fromMap(fieldValues)))
    .unit

  override def hSetNx[T, K, V](key: String, field: K, value: T)(implicit
    codec: RCodec[K, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Boolean] = ZIO
    .fromCompletionStage(map(key).fastPutIfAbsentAsync(field, codec.encode(value)))
    .map(Boolean.unbox)

  override def hStrLen[K](key: String, field: K)(implicit
    codec: RCodec[K, _]
  ): Task[Int] = ZIO.fromCompletionStage(map(key).valueSizeAsync(field)).map(_.toInt)

  override def hTtl[K](key: String, field: K)(implicit
    codec: RCodec[K, _]
  ): Task[Option[Duration]] = ZIO
    .fromCompletionStage(mapCacheNative(key).remainTimeToLiveAsync(field))
    .map(JavaDecoders.fromMillis)

  override def hTtl[K](key: String, fields: Set[K])(implicit
    codec: RCodec[K, _]
  ): Task[Map[K, Option[Duration]]] = ZIO
    .fromCompletionStage(mapCacheNative(key).remainTimeToLiveAsync(fields.asJava))
    .map(JavaDecoders.fromMapMillis)

  override def hVals[V](key: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V] = new ResultBuilder8[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Iterable[T]] = ZIO
      .fromCompletionStage(map(key).readAllValuesAsync())
      .flatMap(JavaDecoders.fromCollection(_))
  }

}

case class RedisHashOperationsLive(redissonClient: RedissonClient) extends RedisHashOperationsImpl

object RedisHashOperations {

  val live: URLayer[RedissonClient, RedisHashOperations] =
    ZLayer.fromFunction(RedisHashOperationsLive.apply _)

}
