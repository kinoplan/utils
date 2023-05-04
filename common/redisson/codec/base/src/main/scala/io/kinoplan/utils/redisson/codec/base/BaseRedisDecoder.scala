package io.kinoplan.utils.redisson.codec.base

import scala.util.Try

/** Defines a contract for decoding values of type `V` from Redis into type `T`.
  *
  * @tparam V
  *   Type of the value retrieved from Redis.
  * @tparam T
  *   Type of the decoded value.
  */
trait BaseRedisDecoder[V, T] {

  /** Decodes a value from Redis format to a usable format.
    *
    * @param value
    *   The input value of type `V` to be decoded.
    * @return
    *   A `Try` containing the decoded value of type `T`, or a failure if decoding fails.
    */
  def decode(value: V): Try[T]
}

/** Companion object for `BaseRedisDecoder` providing utility methods for decoder creation and
  * retrieval.
  */
object BaseRedisDecoder {

  /** Retrieves an implicitly available decoder for a given type.
    *
    * @tparam V
    *   Type of the value retrieved from Redis.
    * @tparam T
    *   Type of the decoded value.
    * @param decoder
    *   The decoder instance that converts `V` to `T`.
    * @return
    *   The available `BaseRedisDecoder` instance for types `T` and `V`.
    */
  def apply[V, T](implicit
    decoder: BaseRedisDecoder[V, T]
  ): BaseRedisDecoder[V, T] = decoder

  /** Creates a `BaseRedisDecoder` using a provided function.
    *
    * @tparam V
    *   Type of the value retrieved from Redis.
    * @tparam T
    *   Type of the decoded value.
    * @param f
    *   The function defining how to decode `V` into `T`.
    * @return
    *   A new `BaseRedisDecoder` instance for types `T` and `V`.
    */
  def create[V, T](f: V => Try[T]): BaseRedisDecoder[V, T] = (value: V) => f(value)

}
