package net.surguy.floodtweet

import org.quartz.{JobExecutionContext, Job}
import models.{HarvestTargets, Stations, Measurements}

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
  private val previousCount = 10

  def scrapeAll() { HarvestTargets.all().foreach( s => scrape(s.stationId) ) }

  def scrape(stationId: Long) {
    log.debug("Starting scrape for station "+stationId)
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
