package io.kinoplan.utils.redisson.codec.base

/** Defines a contract for encoding values of type `T` into `V` suitable for Redis operations.
  *
  * @tparam T
  *   Type of the input value to be encoded.
  * @tparam V
  *   Type of the encoded value suitable for Redis.
  */
trait BaseRedisEncoder[T, V] {

  /** Encodes a value into a format suitable for Redis.
    *
    * @param value
    *   The input value of type `T` to be encoded.
    * @return
    *   The encoded value of type `V`.
    */
  def encode(value: T): V
}

/** Companion object for `BaseRedisEncoder` providing utility methods for encoder creation and
  * retrieval.
  */
object BaseRedisEncoder {

  /** Retrieves an implicitly available encoder for a given type.
    *
    * @tparam T
    *   Type of the input value to be encoded.
    * @tparam V
    *   Type of the encoded value suitable for Redis.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @return
    *   The available `BaseRedisEncoder` instance for types `T` and `V`.
    */
  def apply[T, V](implicit
    encoder: BaseRedisEncoder[T, V]
  ): BaseRedisEncoder[T, V] = encoder

  /** Creates a `BaseRedisEncoder` using a provided function.
    *
    * @tparam T
    *   Type of the input value to be encoded.
    * @tparam V
    *   Type of the encoded value suitable for Redis.
    * @param f
    *   The function defining how to encode `T` into `V`.
    * @return
    *   A new `BaseRedisEncoder` instance for types `T` and `V`.
    */
  def create[T, V](f: T => V): BaseRedisEncoder[T, V] = (value: T) => f(value)

}
