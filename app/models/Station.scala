package models

import play.api.db.DB
import scala.slick.driver.H2Driver.simple._
import slick.session.Database

/**
 * A measuring station.
 */
case class Station(id: Long, name: String, watercourse: String)

object StationTable extends Table[(Long, String, String)]("station") {
  def id = column[Long]("id", O PrimaryKey)
  def name = column[String]("name", O NotNull)
  def watercourse = column[String]("watercourse", O NotNull)

  def * = id ~ name ~ watercourse
}

object Stations {
  import play.api.Play.current
  def database = Database.forDataSource(DB.getDataSource())

  def all(): List[Station] = database.withSession { implicit s: Session =>
    (for(t <- StationTable) yield t.*).list.map( toStation )
  }

  def create(station: Station): Option[Station] = { database.withSession { implicit s: Session =>
    StationTable.*.insert( station.id, station.name, station.watercourse )
    Some(station)
  }}

  def createOrUpdate(station: Station): Option[Station] = { database.withSession { implicit s: Session =>
    val existingStation = (for(t <- StationTable if t.id === station.id ) yield t.*).firstOption.map( toStation )
    existingStation match {
      case Some(_) => existingStation
      case None => create(station)
    }
  }}

  private def toStation(t: (Long, String, String)) : Station =
    Station(t._1, t._2, t._3 )

}
