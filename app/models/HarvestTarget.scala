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

  def create(stationId: Long): Option[HarvestTarget] = { database.withSession { implicit s: Session =>
    val existingTarget = (for(t <- HarvestTargetTable if t.stationId === stationId ) yield t.*).firstOption
    existingTarget match {
      case Some(_) => existingTarget
      case None =>
        val target = HarvestTarget(Guid.next, stationId)
        HarvestTargetTable.*.insert(target)
        Some(target)
    }
  }}

  def delete(stationId: Long)= { database.withSession { implicit s: Session =>
    HarvestTargetTable.where(_.stationId === stationId).delete
  }}

}
