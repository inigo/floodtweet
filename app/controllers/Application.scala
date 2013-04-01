package controllers

import play.api.mvc._
import net.surguy.floodtweet.{Logging, Formatter, ScraperTrigger}
import models.{Station, Measurements, Stations}

object Application extends Controller with Logging {
  private val formatter = new Formatter()

  def index = Action {
    val stationMessages: List[(Station, String)] = Stations.all().map(s => (s, formatter.formatMessage(s, Measurements.lastN(s.id, 10))))
    Ok(views.html.index(stationMessages))
  }

  def trigger = Action {
    log.info("User triggered scrapeAll")
    ScraperTrigger.scraper.scrapeAll()
    Redirect("/")
  }
}