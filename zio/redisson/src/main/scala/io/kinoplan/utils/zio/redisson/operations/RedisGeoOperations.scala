package io.kinoplan.utils.zio.redisson.operations

import io.kinoplan.utils.redisson.codec.{RedisDecoder, RedisEncoder}
import io.kinoplan.utils.zio.redisson.codec.RCodec
import io.kinoplan.utils.zio.redisson.utils.JavaDecoders
import org.redisson.api.geo.GeoSearchArgs
import org.redisson.api.{GeoEntry, GeoPosition, GeoUnit, RGeo, RedissonClient}
import zio.{Task, URLayer, ZIO, ZLayer}

import scala.jdk.CollectionConverters.MapHasAsScala

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
    codec: RCodec
  ): Task[Long]

  /** Adds a single geospatial entry to the sorted set at the specified key.
    *
    * Similar to the GEOADD command.
    *
    * @tparam T
    *   Type of the member to be added, requires RedisEncoder[T].
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
    * @return
    *   The number of elements added to the sorted set, excluding existing elements.
    */
  def geoAdd[T: RedisEncoder](key: String, longitude: Double, latitude: Double, member: T)(implicit
    codec: RCodec
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
    codec: RCodec
  ): Task[Long]

  /** Adds a geospatial entry only if it already exists, to the sorted set at the specified key.
    *
    * Similar to the GEOADD command with the XX option.
    *
    * @tparam T
    *   Type of the member to be added, requires RedisEncoder[T].
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
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def geoAddXx[T: RedisEncoder](key: String, longitude: Double, latitude: Double, member: T)(
    implicit
    codec: RCodec
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
    codec: RCodec
  ): Task[Long]

  /** Adds a geospatial entry only if it does not exist, to the sorted set at the specified key.
    *
    * Similar to the GEOADD command with the NX option.
    *
    * @tparam T
    *   Type of the member to be added, requires RedisEncoder[T].
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
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def geoAddNx[T: RedisEncoder](key: String, longitude: Double, latitude: Double, member: T)(
    implicit
    codec: RCodec
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
    * @return
    *   The distance between the two members in the specified unit.
    */
  def geoDist(
    key: String,
    firstMember: String,
    secondMember: String,
    geoUnit: GeoUnit = GeoUnit.METERS
  )(implicit
    codec: RCodec
  ): Task[Double]

  /** Retrieves geohash strings for the specified members from the geospatial index at the specified
    * key.
    *
    * Similar to the GEOHASH command.
    *
    * @tparam T
    *   Type of the members result mapped to, requires RedisDecoder[T].
    * @param key
    *   The key of the sorted set.
    * @param members
    *   The members to retrieve geohash strings for.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A map of members to their geohash strings.
    */
  def geoHash[T: RedisDecoder](key: String, members: Seq[String])(implicit
    codec: RCodec
  ): Task[Map[T, String]]

  /** Retrieves the geographical positions for the specified members from the geospatial index at
    * the specified key.
    *
    * Similar to the GEOPOS command.
    *
    * @tparam T
    *   Type of the result mapped to positions, requires RedisDecoder[T].
    * @param key
    *   The key of the sorted set.
    * @param members
    *   The members to retrieve positions for.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A map of members to their geographical positions.
    */
  def geoPos[T: RedisDecoder](key: String, members: Seq[String])(implicit
    codec: RCodec
  ): Task[Map[String, GeoPosition]]

  /** Searches for members in the geospatial index at the specified key based on search criteria.
    *
    * Similar to the GEOSEARCH command.
    *
    * @tparam T
    *   Type of the decoded members, requires RedisDecoder[T].
    * @param key
    *   The key of the sorted set.
    * @param args
    *   The search criteria specified as `GeoSearchArgs`.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A list of members matching the search criteria.
    */
  def geoSearch[T: RedisDecoder](key: String, args: GeoSearchArgs)(implicit
    codec: RCodec
  ): Task[List[T]]

  /** Searches for members with their distances in the geospatial index at the specified key using
    * search criteria.
    *
    * Similar to the GEOSEARCH command with the WITHDIST option.
    *
    * @tparam T
    *   Type of the decoded members, requires RedisDecoder[T].
    * @param key
    *   The key of the sorted set.
    * @param args
    *   The search criteria specified as `GeoSearchArgs`.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A map of members to their distances from the reference location.
    */
  def geoSearchWithDist[T: RedisDecoder](key: String, args: GeoSearchArgs)(implicit
    codec: RCodec
  ): Task[Map[T, Double]]

  /** Searches for members with their geographical coordinates in the geospatial index at the
    * specified key using search criteria.
    *
    * Similar to the GEOSEARCH command with the WITHCOORD option.
    *
    * @tparam T
    *   Type of the result mapped to coordinates, requires RedisDecoder[T].
    * @param key
    *   The key of the sorted set.
    * @param args
    *   The search criteria specified as `GeoSearchArgs`.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A map of members to their geographical positions.
    */
  def geoSearchWithCoord[T: RedisDecoder](key: String, args: GeoSearchArgs)(implicit
    codec: RCodec
  ): Task[Map[String, GeoPosition]]

  /** Stores the results of a geospatial search to a destination key.
    *
    * Similar to the GEOSEARCHSTORE command.
    *
    * @tparam T
    *   Type of the decoded members, requires RedisDecoder[T].
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
  def geoSearchStore[T: RedisDecoder](key: String, destination: String, args: GeoSearchArgs)(
    implicit
    codec: RCodec
  ): Task[Long]

}

trait RedisGeoOperationsImpl extends RedisGeoOperations {

  protected val redissonClient: RedissonClient

  private def geo(key: String)(implicit
    codec: RCodec
  ): RGeo[String] = codec
    .underlying
    .map(redissonClient.getGeo[String](key, _))
    .getOrElse(redissonClient.getGeo[String](key))

  override def geoAdd(key: String, entries: Seq[GeoEntry])(implicit
    codec: RCodec
  ): Task[Long] = ZIO.fromCompletionStage(geo(key).addAsync(entries: _*)).map(_.toLong)

  override def geoAdd[T: RedisEncoder](key: String, longitude: Double, latitude: Double, member: T)(
    implicit
    codec: RCodec
  ): Task[Long] = ZIO
    .fromCompletionStage(geo(key).addAsync(longitude, latitude, RedisEncoder[T].encode(member)))
    .map(_.toLong)

  override def geoAddXx(key: String, entries: Seq[GeoEntry])(implicit
    codec: RCodec
  ): Task[Long] = ZIO.fromCompletionStage(geo(key).addIfExistsAsync(entries: _*)).map(_.toLong)

  override def geoAddXx[T: RedisEncoder](key: String, longitude: Double, latitude: Double, member: T)(
    implicit
    codec: RCodec
  ): Task[Boolean] = ZIO
    .fromCompletionStage(
      geo(key).addIfExistsAsync(longitude, latitude, RedisEncoder[T].encode(member))
    )
    .map(Boolean.unbox)

  override def geoAddNx(key: String, entries: Seq[GeoEntry])(implicit
    codec: RCodec
  ): Task[Long] = ZIO.fromCompletionStage(geo(key).tryAddAsync(entries: _*)).map(_.toLong)

  override def geoAddNx[T: RedisEncoder](key: String, longitude: Double, latitude: Double, member: T)(
    implicit
    codec: RCodec
  ): Task[Boolean] = ZIO
    .fromCompletionStage(geo(key).tryAddAsync(longitude, latitude, RedisEncoder[T].encode(member)))
    .map(Boolean.unbox)

  override def geoDist(key: String, firstMember: String, secondMember: String, geoUnit: GeoUnit)(
    implicit
    codec: RCodec
  ): Task[Double] = ZIO
    .fromCompletionStage(geo(key).distAsync(firstMember, secondMember, geoUnit))
    .map(_.toDouble)

  override def geoHash[T: RedisDecoder](key: String, members: Seq[String])(implicit
    codec: RCodec
  ): Task[Map[T, String]] = ZIO
    .fromCompletionStage(geo(key).hashAsync(members: _*))
    .flatMap(JavaDecoders.fromMapKey(_))

  override def geoPos[T: RedisDecoder](key: String, members: Seq[String])(implicit
    codec: RCodec
  ): Task[Map[String, GeoPosition]] = ZIO
    .fromCompletionStage(geo(key).posAsync(members: _*))
    .map(_.asScala.toMap)

  override def geoSearch[T: RedisDecoder](key: String, args: GeoSearchArgs)(implicit
    codec: RCodec
  ): Task[List[T]] = ZIO
    .fromCompletionStage(geo(key).searchAsync(args))
    .flatMap(JavaDecoders.fromList(_))

  override def geoSearchWithDist[T: RedisDecoder](key: String, args: GeoSearchArgs)(implicit
    codec: RCodec
  ): Task[Map[T, Double]] = ZIO
    .fromCompletionStage(geo(key).searchWithDistanceAsync(args))
    .flatMap(JavaDecoders.fromMapScored(_))

  override def geoSearchWithCoord[T: RedisDecoder](key: String, args: GeoSearchArgs)(implicit
    codec: RCodec
  ): Task[Map[String, GeoPosition]] = ZIO
    .fromCompletionStage(geo(key).searchWithPositionAsync(args))
    .map(_.asScala.toMap)

  override def geoSearchStore[T: RedisDecoder](key: String, destination: String, args: GeoSearchArgs)(
    implicit
    codec: RCodec
  ): Task[Long] = ZIO
    .fromCompletionStage(geo(key).storeSearchToAsync(destination, args))
    .map(_.toLong)

}

case class RedisGeoOperationsLive(redissonClient: RedissonClient) extends RedisGeoOperationsImpl

object RedisGeoOperations {

  val live: URLayer[RedissonClient, RedisGeoOperations] =
    ZLayer.fromZIO(ZIO.serviceWith[RedissonClient](RedisGeoOperationsLive))

}
