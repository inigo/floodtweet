package models

import scala.slick.driver.H2Driver.simple._
import java.sql.Timestamp
import org.joda.time.DateTime
import scala.Predef._
import scala.{Double, Long, Some}

/**
 * An individual river level measurement.
 */
case class Measurement(guid: String, stationId: Long, takenAt: DateTime, level: Double, typicalLow: Double, typicalHigh: Double)
object Measurement {
  // Can't use apply and unapply here, since they're already defined in the case class, and we need to change the type of Timestamp
  def toMeasurement(t: (String, Long, Timestamp, Double, Double, Double)) : Measurement =
    Measurement(t._1, t._2, new DateTime(t._3.getTime), t._4, t._5, t._6 )

  def fromMeasurement(m: Measurement) : Option[(String, Long, Timestamp, Double, Double, Double)] =
    Some((m.guid, m.stationId, new Timestamp(m.takenAt.getMillis), m.level, m.typicalLow, m.typicalHigh))
}

object MeasurementTable extends Table[Measurement]("measurement") {
  def guid = column[String]("guid", O PrimaryKey)
  def stationId = column[Long]("stationId", O NotNull)
  def takenAt = column[Timestamp]("takenAt", O NotNull)

  def level = column[Double]("level", O NotNull)
  def typicalLow= column[Double]("typicalLow", O NotNull)
  def typicalHigh = column[Double]("typicalHigh", O NotNull)

  def * = guid ~ stationId ~ takenAt ~ level ~ typicalLow ~ typicalHigh <> (Measurement.toMeasurement _, Measurement.fromMeasurement _)

  def fkDocumentTable = foreignKey("station_fk", stationId, StationTable)(_.id)
}

object Measurements extends UsesDatabase {

  def all(): List[Measurement] = database.withSession { implicit s: Session =>
    (for(t <- MeasurementTable) yield t.*).list
  }

  def allForStation(stationId: Long): List[Measurement] = database.withSession { implicit s: Session =>
    (for(t <- MeasurementTable if t.stationId === stationId) yield t.*).list
  }

  def since(stationId: Long, since: DateTime): List[Measurement] = database.withSession { implicit s: Session =>
    (for(t <- MeasurementTable.sortBy(_.takenAt) if t.stationId === stationId && t.takenAt > new Timestamp(since.getMillis)) yield t.*).list
  }

  def lastN(stationId: Long, count: Int): List[Measurement] = database.withSession { implicit s: Session =>
    (for(t <- MeasurementTable.sortBy(_.takenAt.desc) if t.stationId === stationId) yield t.*).take(count).list.reverse
  }

  def lastMeasurementFor(stationId: Long): Option[Measurement] = lastN(stationId, 1).headOption

  def create(m: Measurement): Option[Measurement] = { database.withSession { implicit s: Session =>
    MeasurementTable.*.insert( m )
    Some(m)
  }}

}
