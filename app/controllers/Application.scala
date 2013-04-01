package controllers

import play.api.mvc._
import net.surguy.floodtweet.{Logging, Formatter, ScraperTrigger}
import models._
import models.Station

object Application extends Controller with Logging {
  private val formatter = new Formatter()

  def index = Action {
    val stationMessages: List[(Station, String)] = Stations.all().map(s => (s, formatter.formatMessage(s, Measurements.lastN(s.id, 10))))
    val targets = HarvestTargets.all()
    Ok(views.html.index(stationMessages, targets))
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