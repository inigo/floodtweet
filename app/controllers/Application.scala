package controllers

import play.api.mvc._
import net.surguy.floodtweet.ScraperTrigger

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index(ScraperTrigger.scraper.getLatest))
  }

  def trigger = Action {
    ScraperTrigger.scraper.scrapeAll()
    Redirect("/")
  }
}