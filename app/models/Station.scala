package models

import scala.slick.driver.H2Driver.simple._

/**
 * A measuring station.
 */
case class Station(id: Long, name: String, watercourse: String)

object StationTable extends Table[Station]("station") {
  def id = column[Long]("id", O PrimaryKey)
  def name = column[String]("name", O NotNull)
  def watercourse = column[String]("watercourse", O NotNull)

  def * = id ~ name ~ watercourse  <> (Station, Station.unapply _)
}

object Stations extends UsesDatabase {

  def all(): List[Station] = database.withSession { implicit s: Session =>
    (for(t <- StationTable) yield t.*).list
  }

  def create(station: Station): Option[Station] = { database.withSession { implicit s: Session =>
    StationTable.*.insert( station )
    Some(station)
  }}

  def createOrUpdate(station: Station): Option[Station] = { database.withSession { implicit s: Session =>
    val existingStation = (for(t <- StationTable if t.id === station.id ) yield t.*).firstOption
    existingStation match {
      case Some(_) => existingStation
      case None => create(station)
    }
  }}

}
