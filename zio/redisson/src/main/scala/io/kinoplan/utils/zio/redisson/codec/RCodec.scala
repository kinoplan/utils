package io.kinoplan.utils.zio.redisson.codec

import scala.util.Try

import org.redisson.client.codec._

import io.kinoplan.utils.redisson.codec.base.{BaseRedisDecoder, BaseRedisEncoder}

/** Encapsulates encoding and decoding logic for Redis operations with customizable key and value
  * types.
  *
  * @param underlying
  *   Optional base codec for Redisson interactions.
  *
  * @tparam K
  *   Type of the keys.
  * @tparam V
  *   Type of the values.
  */
case class RCodec[K, V] private (underlying: Option[Codec]) {

  /** Encodes a value using the provided encoder.
    *
    * @param value
    *   The value to encode.
    * @param encoder
    *   Implicit encoder for given value-type pair.
    * @tparam T
    *   Type of the value to encode.
    * @return
    *   Encoded value of type V.
    */
  def encode[T](value: T)(implicit
    encoder: BaseRedisEncoder[T, V]
  ): V = encoder.encode(value)

  /** Decodes a value using the provided decoder.
    *
    * @param value
    *   The value to decode.
    * @param decoder
    *   Implicit decoder for given value-type pair.
    * @tparam T
    *   Target type of the decoded value.
    * @return
    *   Attempt to decode value as type T, encapsulated in Try.
    */
  def decode[T](value: V)(implicit
    decoder: BaseRedisDecoder[V, T]
  ): Try[T] = decoder.decode(value)

  /** Converts this codec to use Long values for decoding.
    *
    * @return
    *   A new RCodec instance with Long values.
    */
  def toLongValue: RCodec[K, java.lang.Long] = RCodec[K, java.lang.Long](underlying)

  /** Converts this codec to use Double values for decoding.
    *
    * @return
    *   A new RCodec instance with Double values.
    */
  def toDoubleValue: RCodec[K, java.lang.Double] = RCodec[K, java.lang.Double](underlying)
}

/** Factory methods and predefined codecs for RCodec.
  */
object RCodec {

  /** A dummy codec for default usage. */
  implicit val dummyCodec: RCodec[String, String] = RCodec(None)

  /** Predefined codec for String values. */
  val stringCodec: RCodec[String, String] = create(StringCodec.INSTANCE)

  /** Predefined codec for Integer values. */
  val intCodec: RCodec[String, String] = create(IntegerCodec.INSTANCE)

  /** Predefined codec for Long values. */
  val longCodec: RCodec[String, String] = create(LongCodec.INSTANCE)

  /** Predefined codec for Double values. */
  val doubleCodec: RCodec[String, String] = create(DoubleCodec.INSTANCE)

  /** Predefined codec for byte array values. */
  val byteArrayCodec: RCodec[String, String] = create(ByteArrayCodec.INSTANCE)

  /** Creates a new RCodec with a specified codec.
    *
    * @param codec
    *   The base codec for encoding and decoding operations.
    * @tparam K
    *   Type of the keys.
    * @tparam V
    *   Type of the values.
    * @return
    *   A new RCodec instance with the specified codec.
    */
  def create[K, V](codec: Codec): RCodec[K, V] = create(Some(codec))

  /** Creates a new RCodec with an optional codec.
    *
    * @param codec
    *   Optional base codec for encoding and decoding operations.
    * @tparam K
    *   Type of the keys.
    * @tparam V
    *   Type of the values.
    * @return
    *   A new RCodec instance with the specified codec if provided.
    */
  def create[K, V](codec: Option[Codec] = None): RCodec[K, V] = RCodec(codec)
}
