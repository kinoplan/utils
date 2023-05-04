package io.kinoplan.utils.zio.redisson.operations.base

import org.redisson.api.{GeoPosition, StreamMessageId}
import zio.Task
import zio.stream.ZStream

import io.kinoplan.utils.redisson.codec.base.BaseRedisDecoder

/** A set of ResultBuilder traits for decoding Redis command results into various data structures.
  */
sealed trait ResultBuilder

object ResultBuilder {

  /** Builds a result as an optional single decoded value.
    *
    * @tparam V
    *   Type of the raw Redis value.
    */
  trait ResultBuilder1[V] extends ResultBuilder {

    /** Decodes a value using the provided decoder.
      *
      * @tparam T
      *   Target type of the decoded value.
      * @param decoder
      *   Decoder responsible for transforming the value.
      * @return
      *   An optional decoded value.
      */
    def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Option[T]]

  }

  /** Builds a result as an optional single decoded string value.
    */
  trait ResultBuilder2 extends ResultBuilder {

    /** Decodes a string value using the provided decoder.
      *
      * @tparam T
      *   Target type of the decoded value.
      * @param decoder
      *   Decoder responsible for transforming the value.
      * @return
      *   An optional decoded value.
      */
    def as[T](implicit
      decoder: BaseRedisDecoder[String, T]
    ): Task[Option[T]]

  }

  /** Builds a result as a map of string keys to decoded values.
    *
    * @tparam V
    *   Type of the raw Redis value.
    */
  trait ResultBuilder3[V] extends ResultBuilder {

    /** Decodes map values using the provided decoder.
      *
      * @tparam T
      *   Target type of the decoded values.
      * @param decoder
      *   Decoder responsible for transforming the values.
      * @return
      *   A map of decoded values by string keys.
      */
    def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Map[String, T]]

  }

  /** Builds a result as a list of decoded values.
    *
    * @tparam V
    *   Type of the raw Redis value.
    */
  trait ResultBuilder4[V] extends ResultBuilder {

    /** Decodes a list of values using the provided decoder.
      *
      * @tparam T
      *   Target type of the decoded values.
      * @param decoder
      *   Decoder responsible for transforming the values.
      * @return
      *   A list of decoded values.
      */
    def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[List[T]]

  }

  /** Builds a result as a map from decoded values to scores.
    *
    * @tparam V
    *   Type of the raw Redis value.
    */
  trait ResultBuilder5[V] extends ResultBuilder {

    /** Decodes map keys and associates them with doubles.
      *
      * @tparam T
      *   Target type of the decoded keys.
      * @param decoder
      *   Decoder responsible for transforming the keys.
      * @return
      *   A map of decoded values to scores.
      */
    def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Map[T, Double]]

  }

  /** Builds a result as a map from decoded values to geographical positions.
    *
    * @tparam V
    *   Type of the raw Redis value.
    */
  trait ResultBuilder6[V] extends ResultBuilder {

    /** Decodes map keys for GeoPosition associations.
      *
      * @tparam T
      *   Target type of the decoded keys.
      * @param decoder
      *   Decoder responsible for transforming the keys.
      * @return
      *   A map of decoded values to GeoPositions.
      */
    def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Map[T, GeoPosition]]

  }

  /** Builds a result as a map from existing keys to decoded values.
    *
    * @tparam K
    *   Type of the raw Redis key.
    * @tparam V
    *   Type of the raw Redis value.
    */
  trait ResultBuilder7[K, V] extends ResultBuilder {

    /** Decodes values mapped to existing keys.
      *
      * @tparam T
      *   Target type of the decoded values.
      * @param decoder
      *   Decoder responsible for transforming the values.
      * @return
      *   A map of keys to decoded values.
      */
    def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Map[K, T]]

  }

  /** Builds a result as an iterable collection of decoded values.
    *
    * @tparam V
    *   Type of the raw Redis value.
    */
  trait ResultBuilder8[V] extends ResultBuilder {

    /** Decodes a list of values using the provided decoder.
      *
      * @tparam T
      *   Target type of the decoded values.
      * @param decoder
      *   Decoder responsible for transforming the values.
      * @return
      *   An iterable of decoded values.
      */
    def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Iterable[T]]

  }

  /** Builds a streaming result as key-value tuples.
    *
    * @tparam K
    *   Type of the raw Redis key.
    * @tparam V
    *   Type of the raw Redis value.
    */
  trait ResultBuilder9[K, V] extends ResultBuilder {

    /** Streams pairs of keys to decoded values.
      *
      * @tparam T
      *   Target type of the decoded values.
      * @param decoder
      *   Decoder responsible for transforming the values.
      * @return
      *   A stream providing key to decoded value pairs.
      */
    def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): ZStream[Any, Throwable, (K, T)]

  }

  /** Builds a streaming result of decoded values.
    *
    * @tparam V
    *   Type of the raw Redis value.
    */
  trait ResultBuilder10[V] extends ResultBuilder {

    /** Streams decoded values using the provided decoder.
      *
      * @tparam T
      *   Target type of the decoded values.
      * @param decoder
      *   Decoder responsible for transforming the values.
      * @return
      *   A stream providing decoded values.
      */
    def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): ZStream[Any, Throwable, T]

  }

  /** Builds a result as a single decoded value.
    *
    * @tparam V
    *   Type of the raw Redis value.
    */
  trait ResultBuilder11[V] extends ResultBuilder {

    /** Decodes a single value using the provided decoder.
      *
      * @tparam T
      *   Target type of the decoded value.
      * @param decoder
      *   Decoder responsible for transforming the value.
      * @return
      *   A decoded value.
      */
    def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[T]

  }

  /** Builds a result as a map of string keys to lists of decoded values.
    *
    * @tparam V
    *   Type of the raw Redis value.
    */
  trait ResultBuilder12[V] extends ResultBuilder {

    /** Decodes values into lists mapped by string keys.
      *
      * @tparam T
      *   Target type of the decoded values.
      * @param decoder
      *   Decoder responsible for transforming the values.
      * @return
      *   A map of lists of decoded values by string keys.
      */
    def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Map[String, List[T]]]

  }

  /** Builds a result as an iterable collection of optional decoded values.
    *
    * @tparam V
    *   Type of the raw Redis value.
    */
  trait ResultBuilder13[V] extends ResultBuilder {

    /** Decodes values wrapped in options.
      *
      * @tparam T
      *   Target type of the decoded values.
      * @param decoder
      *   Decoder responsible for transforming the values.
      * @return
      *   An iterable of optional decoded values.
      */
    def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Iterable[Option[T]]]

  }

  /** Builds a result as a set of decoded values.
    *
    * @tparam V
    *   Type of the raw Redis value.
    */
  trait ResultBuilder14[V] extends ResultBuilder {

    /** Decodes values into a set using the provided decoder.
      *
      * @tparam T
      *   Target type of the decoded values.
      * @param decoder
      *   Decoder responsible for transforming the values.
      * @return
      *   A set of decoded values.
      */
    def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Set[T]]

  }

  /** Builds a result as a nested map of string keys to maps of decoded values and scores.
    *
    * @tparam V
    *   Type of the raw Redis value.
    */
  trait ResultBuilder15[V] extends ResultBuilder {

    /** Decodes values and associates them with scores mapped by string keys.
      *
      * @tparam T
      *   Target type of the decoded values.
      * @param decoder
      *   Decoder responsible for transforming the values.
      * @return
      *   A nested map of decoded values and scores by string keys.
      */
    def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Map[String, Map[T, Double]]]

  }

  /** Builds a result as a map from stream message IDs to maps of keys to decoded values.
    *
    * @tparam K
    *   Type of the raw Redis key.
    * @tparam V
    *   Type of the raw Redis value.
    */
  trait ResultBuilder16[K, V] extends ResultBuilder {

    /** Decodes values mapped by stream message IDs and keys.
      *
      * @tparam T
      *   Target type of the decoded values.
      * @param decoder
      *   Decoder responsible for transforming the values.
      * @return
      *   A map from StreamMessageId to maps of keys to decoded values.
      */
    def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Map[StreamMessageId, Map[K, T]]]

  }

  /** Builds a result as a nested map of stream message IDs to maps of keys to decoded values.
    *
    * @tparam K
    *   Type of the raw Redis key.
    * @tparam V
    *   Type of the raw Redis value.
    */
  trait ResultBuilder17[K, V] extends ResultBuilder {

    /** Decodes values mapped by string, stream message IDs, and keys.
      *
      * @tparam T
      *   Target type of the decoded values.
      * @param decoder
      *   Decoder responsible for transforming the values.
      * @return
      *   A nested map of decoded values by string, StreamMessageId, and key.
      */
    def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Map[String, Map[StreamMessageId, Map[K, T]]]]

  }

}
