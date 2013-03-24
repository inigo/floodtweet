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

object ScraperTrigger extends Logging {
  private val lastMeasurements = new mutable.HashMap[String, Measurement]()
  private val scraper = new EnvironmentAgencyScraper()
  private val tweeter = new Tweeter()

  private val stationIds = List("7075", "7074", "7057")

  def scrapeAll() { stationIds.foreach(scrape) }

  def scrape(stationId: String) {
    scraper.scrapeLevel(stationId) match {
      case (Some(station), Some(measurement)) =>
        if (!lastMeasurements.contains(stationId) || measurement.takenAt.isAfter(lastMeasurements(stationId).takenAt)) {
          log.debug("Retrieved measurement "+measurement)
          lastMeasurements(stationId) = measurement
          if (measurement.currentLevel > measurement.typicalHigh) tweeter.tweet(station, measurement)
        }
      case _ =>
        log.info("Could not scrape "+stationId)
    }
  }

  def getLatest = lastMeasurements.toMap
}
