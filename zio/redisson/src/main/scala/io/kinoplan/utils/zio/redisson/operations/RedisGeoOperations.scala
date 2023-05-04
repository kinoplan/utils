package io.kinoplan.utils.zio.redisson.operations

import org.redisson.api.{GeoEntry, GeoPosition, GeoUnit, RGeo, RedissonClient}
import org.redisson.api.geo.GeoSearchArgs
import zio.{Task, URLayer, ZIO, ZLayer}

import io.kinoplan.utils.redisson.codec.base.{BaseRedisDecoder, BaseRedisEncoder}
import io.kinoplan.utils.zio.redisson.codec.RCodec
import io.kinoplan.utils.zio.redisson.operations.base.ResultBuilder._
import io.kinoplan.utils.zio.redisson.utils.JavaDecoders

/** Interface representing operations that can be performed on Redis geospatial data.
  */
trait RedisGeoOperations {

  /** Adds geospatial entries to the sorted set at the specified key.
    *
    * Similar to the GEOADD command.
    *
    * @param key
    *   The key of the sorted set.
    * @param entries
    *   A sequence of `GeoEntry` objects to add.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The number of elements added to the sorted set, excluding existing elements.
    */
  def geoAdd(key: String, entries: Seq[GeoEntry])(implicit
    codec: RCodec[_, _]
  ): Task[Long]

  /** Adds a single geospatial entry to the sorted set at the specified key.
    *
    * Similar to the GEOADD command.
    *
    * @param key
    *   The key of the sorted set.
    * @param longitude
    *   The longitude of the member's location.
    * @param latitude
    *   The latitude of the member's location.
    * @param member
    *   The member to add.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   The number of elements added to the sorted set, excluding existing elements.
    */
  def geoAdd[T, V](key: String, longitude: Double, latitude: Double, member: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Long]

  /** Adds geospatial entries only if they already exist, to the sorted set at the specified key.
    *
    * Similar to the GEOADD command with the XX option.
    *
    * @param key
    *   The key of the sorted set.
    * @param entries
    *   A sequence of `GeoEntry` objects to update.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The number of elements updated in the sorted set.
    */
  def geoAddXx(key: String, entries: Seq[GeoEntry])(implicit
    codec: RCodec[_, _]
  ): Task[Long]

  /** Adds a geospatial entry only if it already exists, to the sorted set at the specified key.
    *
    * Similar to the GEOADD command with the XX option.
    *
    * @param key
    *   The key of the sorted set.
    * @param longitude
    *   The longitude of the member's location.
    * @param latitude
    *   The latitude of the member's location.
    * @param member
    *   The member to update.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def geoAddXx[T, V](key: String, longitude: Double, latitude: Double, member: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Boolean]

  /** Adds geospatial entries only if they do not exist, to the sorted set at the specified key.
    *
    * Similar to the GEOADD command with the NX option.
    *
    * @param key
    *   The key of the sorted set.
    * @param entries
    *   A sequence of `GeoEntry` objects to add.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The number of elements added to the sorted set.
    */
  def geoAddNx(key: String, entries: Seq[GeoEntry])(implicit
    codec: RCodec[_, _]
  ): Task[Long]

  /** Adds a geospatial entry only if it does not exist, to the sorted set at the specified key.
    *
    * Similar to the GEOADD command with the NX option.
    *
    * @param key
    *   The key of the sorted set.
    * @param longitude
    *   The longitude of the member's location.
    * @param latitude
    *   The latitude of the member's location.
    * @param member
    *   The member to add.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def geoAddNx[T, V](key: String, longitude: Double, latitude: Double, member: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Boolean]

  /** Calculates the distance between two members in the geospatial index at the specified key.
    *
    * Similar to the GEODIST command.
    *
    * @param key
    *   The key of the sorted set.
    * @param firstMember
    *   The first member to calculate the distance from.
    * @param secondMember
    *   The second member to calculate the distance to.
    * @param geoUnit
    *   The unit of measurement for the distance. Default is meters.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   The distance between the two members in the specified unit.
    */

  def geoDist[T, V](key: String, firstMember: T, secondMember: T, geoUnit: GeoUnit = GeoUnit.METERS)(
    implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Double]

  /** Retrieves geohash strings for the specified members from the geospatial index at the specified
    * key.
    *
    * Similar to the GEOHASH command.
    *
    * @param key
    *   The key of the sorted set.
    * @param members
    *   The members to retrieve geohash strings for.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @param decoder
    *   The decoder instance that converts `V` to `T`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A map of members to their geohash strings.
    */
  def geoHash[T, V](key: String, members: Seq[T])(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V],
    decoder: BaseRedisDecoder[V, T]
  ): Task[Map[T, String]]

  /** Retrieves the geographical positions for the specified members from the geospatial index at
    * the specified key.
    *
    * Similar to the GEOPOS command.
    *
    * @param key
    *   The key of the sorted set.
    * @param members
    *   The members to retrieve positions for.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @param decoder
    *   The decoder instance that converts `V` to `T`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A map of members to their geographical positions.
    */
  def geoPos[T, V](key: String, members: Seq[T])(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V],
    decoder: BaseRedisDecoder[V, T]
  ): Task[Map[T, GeoPosition]]

  /** Searches for members in the geospatial index at the specified key based on search criteria.
    *
    * Similar to the GEOSEARCH command.
    *
    * @param key
    *   The key of the sorted set.
    * @param args
    *   The search criteria specified as `GeoSearchArgs`.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A list of members matching the search criteria.
    */
  def geoSearch[V](key: String, args: GeoSearchArgs)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder4[V]

  /** Searches for members with their distances in the geospatial index at the specified key using
    * search criteria.
    *
    * Similar to the GEOSEARCH command with the WITHDIST option.
    *
    * @param key
    *   The key of the sorted set.
    * @param args
    *   The search criteria specified as `GeoSearchArgs`.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A map of members to their distances from the reference location.
    */
  def geoSearchWithDist[V](key: String, args: GeoSearchArgs)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder5[V]

  /** Searches for members with their geographical coordinates in the geospatial index at the
    * specified key using search criteria.
    *
    * Similar to the GEOSEARCH command with the WITHCOORD option.
    *
    * @param key
    *   The key of the sorted set.
    * @param args
    *   The search criteria specified as `GeoSearchArgs`.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A map of members to their geographical positions.
    */
  def geoSearchWithCoord[V](key: String, args: GeoSearchArgs)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder6[V]

  /** Stores the results of a geospatial search to a destination key.
    *
    * Similar to the GEOSEARCHSTORE command.
    *
    * @param key
    *   The key of the sorted set.
    * @param destination
    *   The destination key where results are stored.
    * @param args
    *   The search criteria specified as `GeoSearchArgs`.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The number of elements stored in the destination.
    */
  def geoSearchStore(key: String, destination: String, args: GeoSearchArgs)(implicit
    codec: RCodec[_, _]
  ): Task[Long]

}

trait RedisGeoOperationsImpl extends RedisGeoOperations {

  protected val redissonClient: RedissonClient

  private def geo[V](key: String)(implicit
    codec: RCodec[_, V]
  ): RGeo[V] = codec
    .underlying
    .map(redissonClient.getGeo[V](key, _))
    .getOrElse(redissonClient.getGeo[V](key))

  override def geoAdd(key: String, entries: Seq[GeoEntry])(implicit
    codec: RCodec[_, _]
  ): Task[Long] = ZIO.fromCompletionStage(geo(key).addAsync(entries: _*)).map(_.toLong)

  override def geoAdd[T, V](key: String, longitude: Double, latitude: Double, member: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Long] = ZIO
    .fromCompletionStage(geo(key).addAsync(longitude, latitude, codec.encode(member)))
    .map(_.toLong)

  override def geoAddXx(key: String, entries: Seq[GeoEntry])(implicit
    codec: RCodec[_, _]
  ): Task[Long] = ZIO.fromCompletionStage(geo(key).addIfExistsAsync(entries: _*)).map(_.toLong)

  override def geoAddXx[T, V](key: String, longitude: Double, latitude: Double, member: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Boolean] = ZIO
    .fromCompletionStage(geo(key).addIfExistsAsync(longitude, latitude, codec.encode(member)))
    .map(Boolean.unbox)

  override def geoAddNx(key: String, entries: Seq[GeoEntry])(implicit
    codec: RCodec[_, _]
  ): Task[Long] = ZIO.fromCompletionStage(geo(key).tryAddAsync(entries: _*)).map(_.toLong)

  override def geoAddNx[T, V](key: String, longitude: Double, latitude: Double, member: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Boolean] = ZIO
    .fromCompletionStage(geo(key).tryAddAsync(longitude, latitude, codec.encode(member)))
    .map(Boolean.unbox)

  override def geoDist[T, V](key: String, firstMember: T, secondMember: T, geoUnit: GeoUnit)(
    implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Double] = ZIO
    .fromCompletionStage(
      geo(key).distAsync(codec.encode(firstMember), codec.encode(secondMember), geoUnit)
    )
    .map(_.toDouble)

  override def geoHash[T, V](key: String, members: Seq[T])(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V],
    decoder: BaseRedisDecoder[V, T]
  ): Task[Map[T, String]] = ZIO
    .fromCompletionStage(geo(key).hashAsync(members.map(codec.encode(_)): _*))
    .flatMap(JavaDecoders.fromMapKey(_))

  override def geoPos[T, V](key: String, members: Seq[T])(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V],
    decoder: BaseRedisDecoder[V, T]
  ): Task[Map[T, GeoPosition]] = ZIO
    .fromCompletionStage(geo(key).posAsync(members.map(codec.encode(_)): _*))
    .flatMap(JavaDecoders.fromMapKey(_))

  override def geoSearch[V](key: String, args: GeoSearchArgs)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder4[V] = new ResultBuilder4[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[List[T]] = ZIO
      .fromCompletionStage(geo(key).searchAsync(args))
      .flatMap(JavaDecoders.fromList(_))
  }

  override def geoSearchWithDist[V](key: String, args: GeoSearchArgs)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder5[V] = new ResultBuilder5[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Map[T, Double]] = ZIO
      .fromCompletionStage(geo(key).searchWithDistanceAsync(args))
      .flatMap(JavaDecoders.fromMapScored(_))
  }

  override def geoSearchWithCoord[V](key: String, args: GeoSearchArgs)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder6[V] = new ResultBuilder6[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Map[T, GeoPosition]] = ZIO
      .fromCompletionStage(geo(key).searchWithPositionAsync(args))
      .flatMap(JavaDecoders.fromMapKey(_))
  }

  override def geoSearchStore(key: String, destination: String, args: GeoSearchArgs)(implicit
    codec: RCodec[_, _]
  ): Task[Long] = ZIO
    .fromCompletionStage(geo(key).storeSearchToAsync(destination, args))
    .map(_.toLong)

}

case class RedisGeoOperationsLive(redissonClient: RedissonClient) extends RedisGeoOperationsImpl

object RedisGeoOperations {

  val live: URLayer[RedissonClient, RedisGeoOperations] =
    ZLayer.fromFunction(RedisGeoOperationsLive.apply _)

}
