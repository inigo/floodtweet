package models

import play.api.db.DB
import scala.slick.driver.H2Driver.simple._
import slick.session.Database

case class HarvestTarget(guid: String, stationId: Long)

object HarvestTargetTable extends Table[(String, Long)]("harvesttarget") {
  def guid = column[String]("guid", O PrimaryKey)
  def stationId = column[Long]("stationId", O NotNull)

  def * = guid ~ stationId
}

object HarvestTargets {
  import play.api.Play.current
  def database = Database.forDataSource(DB.getDataSource())

  def all(): List[HarvestTarget] = database.withSession { implicit s: Session =>
    (for(t <- HarvestTargetTable) yield t.*).list.map( toHarvestTarget )
  }

  def create(m: HarvestTarget): Option[HarvestTarget] = { database.withSession { implicit s: Session =>
    HarvestTargetTable.*.insert( m.guid, m.stationId )
    Some(m)
  }}

  private def toHarvestTarget(t: (String, Long)) : HarvestTarget =
    HarvestTarget(t._1, t._2)

}
