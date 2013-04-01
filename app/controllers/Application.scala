package controllers

import play.api.mvc._
import net.surguy.floodtweet.{Logging, Formatter, ScraperTrigger}
import models._
import models.Station

object Application extends Controller with Logging {
  private val formatter = new Formatter()

  def index = Action {
    val targets = HarvestTargets.all()
    val allStations = Stations.all()
    val stationMessages: List[(Long, Option[Station], List[Measurement], String)] = targets.map{t =>
      val stationId = t.stationId
      val station = allStations.find(_.id == stationId)
      val recentValues = Measurements.lastN(stationId, 10)
      val message = station match {
        case Some(s) => formatter.formatMessage(s, recentValues)
        case None => "No data"
      }
      (stationId, station, recentValues, message)
    }

    Ok(views.html.index(stationMessages))
  }

  def trigger = Action {
    log.info("User triggered scrapeAll")
    ScraperTrigger.scraper.scrapeAll()
    Redirect("/")
  }

  def addTarget(stationId: Long) = Action {
    log.info("Adding new harvest target")
    HarvestTargets.create(stationId)
    Redirect("/")
  }

  def deleteTarget(stationId: Long) = Action {
    log.info("Adding new harvest target")
    HarvestTargets.delete(stationId)
    Redirect("/")
  }
}