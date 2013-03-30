package net.surguy.floodtweet

import org.quartz.{JobExecutionContext, Job}
import collection.mutable
import models.{Stations, Measurements, Measurement}
import org.joda.time.DateTime

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
  val scraper = new ScraperTrigger(new EnvironmentAgencyScraper(), new Tweeter())
}

class ScraperTrigger(val scraper: EnvironmentAgencyScraper, val tweeter: Tweeter) extends Logging {
  private val stationIds = List(7075L, 7074L, 7057L)

  def scrapeAll() { stationIds.foreach(scrape) }

  def scrape(stationId: Long) {
    scraper.scrapeLevel(stationId) match {
      case (Some(station), Some(measurement)) =>
        val last = Measurements.lastMeasurementFor(stationId)
        if (last.isEmpty || measurement.takenAt.isAfter(last.get.takenAt)) {
          log.debug("Retrieved measurement "+measurement)
          Stations.createOrUpdate(station)
          Measurements.create(measurement)
          if (measurement.level > measurement.typicalHigh) tweeter.tweet(station, measurement)
        }
      case _ =>
        log.info("Could not scrape "+stationId)
    }
  }

  def getLatest: Map[Long, Measurement] = stationIds.map( s =>
      Measurements.lastMeasurementFor(s) match {
        case Some(m) => Some((s, m))
        case None => None
      }
    ).flatten.toMap
  def getLastWeek = stationIds.map( s => (s, Measurements.lastValues(s, daysAgo(7))) ).toMap
  private def daysAgo(days: Integer): DateTime = DateTime.now.toDateMidnight.toDateTime.minusDays(days)

}
