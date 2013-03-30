package models

import play.api.db.DB
import scala.slick.driver.H2Driver.simple._
import java.sql.Timestamp
import org.joda.time.DateTime
import slick.session.Database
import java.util.UUID

/**
 * Database access code.
 */
object MeasurementTable extends Table[(String, Long, Timestamp, Double, Double, Double)]("measurement") {
  def guid = column[String]("guid", O PrimaryKey)
  def stationId = column[Long]("stationId", O NotNull)
  def takenAt = column[Timestamp]("takenAt", O NotNull)

  def level = column[Double]("level", O NotNull)
  def typicalLow= column[Double]("typicalLow", O NotNull)
  def typicalHigh = column[Double]("typicalHigh", O NotNull)

  def * = guid ~ stationId ~ takenAt ~ level ~ typicalLow ~ typicalHigh

  def fkDocumentTable = foreignKey("station_fk", stationId, StationTable)(_.id)
}

/**
 * An individual river level measurement.
 */
case class Measurement(guid: String, stationId: Long, takenAt: DateTime, level: Double, typicalLow: Double, typicalHigh: Double)

object Measurements {
  import play.api.Play.current
  def database = Database.forDataSource(DB.getDataSource())

  def all(): List[Measurement] = database.withSession { implicit s: Session =>
    (for(t <- MeasurementTable) yield t.*).list.map( toMeasurement )
  }

  def allForStation(stationId: Long): List[Measurement] = database.withSession { implicit s: Session =>
    (for(t <- MeasurementTable if t.stationId === stationId) yield t.*).list.map( toMeasurement )
  }

  def lastValues(stationId: Long, since: DateTime): List[Measurement] = database.withSession { implicit s: Session =>
    (for(t <- MeasurementTable.sortBy(_.takenAt) if t.stationId === stationId && t.takenAt > new Timestamp(since.getMillis)) yield t.*)
      .list.map( toMeasurement )
  }

  def lastMeasurementFor(stationId: Long): Option[Measurement] = database.withSession { implicit s: Session =>
    (for(t <- MeasurementTable.sortBy(_.takenAt) if t.stationId === stationId ) yield t.*).firstOption.map( toMeasurement )
  }

  def create(m: Measurement): Option[Measurement] = { database.withSession { implicit s: Session =>
    MeasurementTable.*.insert( m.guid, m.stationId, new Timestamp(m.takenAt.getMillis), m.level, m.typicalLow, m.typicalHigh )
    Some(m)
  }}

  private def toMeasurement(t: (String, Long, Timestamp, Double, Double, Double)) : Measurement =
    Measurement(t._1, t._2, new DateTime(t._3.getTime), t._4, t._5, t._6 )

}
