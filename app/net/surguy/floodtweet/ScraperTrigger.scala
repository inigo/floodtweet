package net.surguy.floodtweet

import org.quartz.{JobExecutionContext, Job}
import collection.mutable

/**
 * Quartz job that launches the "scrape then tweet" process.
 *
 * @author Inigo Surguy
 */
class ScraperTrigger extends Job {
  def execute(context: JobExecutionContext) {
    ScraperTrigger.scrapeAll()
  }
}

object ScraperTrigger {
  private val lastMeasurements = new mutable.HashMap[String, Measurement]()
  private val scraper = new EnvironmentAgencyScraper()
  private val tweeter = new Tweeter()

  private val stationIds = List("7075")

  def scrapeAll() { stationIds.foreach(scrape) }

  def scrape(stationId: String) {
    val (station, measurement) = scraper.scrapeLevel(stationId)
    if (!lastMeasurements.contains(stationId) || measurement.takenAt.isAfter(lastMeasurements(stationId).takenAt)) {
      lastMeasurements(stationId) = measurement
      if (measurement.currentLevel > measurement.typicalHigh) tweeter.tweet(station, measurement)
    }
  }

}
