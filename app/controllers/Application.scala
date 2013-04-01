package controllers

import play.api.mvc._
import net.surguy.floodtweet.{Formatter, ScraperTrigger}
import models.{Station, Measurements, Stations}

object Application extends Controller {
  private val formatter = new Formatter()

  def index = Action {
    val stationMessages: List[(Station, String)] = Stations.all().map(s => (s, formatter.formatMessage(s, Measurements.lastN(s.id, 10))))
    Ok(views.html.index(stationMessages))
  }

  def trigger = Action {
    ScraperTrigger.scraper.scrapeAll()
    Redirect("/")
  }
}