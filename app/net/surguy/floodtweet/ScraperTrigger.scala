package net.surguy.floodtweet

import org.quartz.{JobExecutionContext, Job}
import models.{Stations, Measurements}

/**
 * Quartz job that launches the "scrape then tweet" process.
 *
 * @author Inigo Surguy
 */
class ScraperTriggerJob extends Job {
  def execute(context: JobExecutionContext) {
    ScraperTrigger.scraper.scrapeAll()
  }
}

object ScraperTrigger {
  val scraper = new ScraperTrigger(new EnvironmentAgencyScraper(), new Tweeter(new Formatter()))
}

class ScraperTrigger(val scraper: EnvironmentAgencyScraper, val tweeter: Tweeter) extends Logging {
  private val stationIds = List(7075L, 7074L, 7057L)
  private val previousCount = 10

  def scrapeAll() { stationIds.foreach(scrape) }

  def scrape(stationId: Long) {
    scraper.scrapeLevel(stationId) match {
      case (Some(station), Some(measurement)) =>
        val last = Measurements.lastMeasurementFor(stationId)
        if (last.isEmpty || measurement.takenAt.isAfter(last.get.takenAt)) {
          log.debug("Retrieved new measurement for station "+stationId+" of "+measurement)
          Stations.createOrUpdate(station)
          Measurements.create(measurement)
          val measurements = Measurements.lastN(stationId, previousCount)
          if (measurement.level > measurement.typicalHigh) tweeter.tweet(station, measurements)
        } else {
          log.debug("No new measurements for station "+stationId)
        }
      case _ =>
        log.info("Could not scrape station "+stationId)
    }
  }

}
