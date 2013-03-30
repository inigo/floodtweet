package models

import scala.slick.driver.H2Driver.simple._

/**
 *  A monitoring station that is to be checked.
 */
case class HarvestTarget(guid: String, stationId: Long)

object HarvestTargetTable extends Table[HarvestTarget]("harvesttarget") {
  def guid = column[String]("guid", O PrimaryKey)
  def stationId = column[Long]("stationId", O NotNull)

  def * = guid ~ stationId <> (HarvestTarget, HarvestTarget.unapply _)
}

object HarvestTargets extends UsesDatabase {
  def all(): List[HarvestTarget] = database.withSession { implicit s: Session =>
    (for(t <- HarvestTargetTable) yield t.*).list
  }

  def create(m: HarvestTarget): Option[HarvestTarget] = { database.withSession { implicit s: Session =>
    HarvestTargetTable.*.insert( m )
    Some(m)
  }}
}
