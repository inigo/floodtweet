package net.surguy.floodtweet

import org.specs2.mutable.Specification
import play.api.test.Helpers._
import play.api.test.FakeApplication
import models.{Measurements, Measurement, Station}
import org.specs2.mock.Mockito
import org.joda.time.DateTime

class ScraperTriggerTest extends Specification with Mockito {
  def meas(level: Double, time: DateTime) = Measurement("guid", 7075L, time, level, 1.0, 2.0)

  // A mock scraper that will return the same value the first two times its called, then another value the third
  def mockScraper() = {
    val scraper = mock[EnvironmentAgencyScraper]
    val before = DateTime.now.minusHours(3)
    val now = DateTime.now
    val testStation = Station(7075L, "Test", "River")
    scraper.scrapeLevel(7075L) returns ((Some(testStation), Some(meas(2.1, before)))) thenReturns
      ((Some(testStation), Some(meas(2.1, before)))) thenReturns ((Some(testStation), Some(meas(2.2, now))))
    scraper
  }

  "scraping a river" should {
    "send a tweet, but only when there are new measurements" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val tweeter = mock[Tweeter]
        val scraperTrigger = new ScraperTrigger(mockScraper(), tweeter)
        scraperTrigger.scrape(7075L)
        there was one(tweeter).tweet(any[Station], any[Seq[Measurement]])
        scraperTrigger.scrape(7075L)
        there was one(tweeter).tweet(any[Station], any[Seq[Measurement]])
        scraperTrigger.scrape(7075L)
        there were two(tweeter).tweet(any[Station], any[Seq[Measurement]])
      }
    }
    "not store duplicate measurements in the database" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val scraperTrigger = new ScraperTrigger(mockScraper(), mock[Tweeter])
        scraperTrigger.scrape(7075L)
        Measurements.all() must haveSize(1)
        scraperTrigger.scrape(7075L)
        Measurements.all() must haveSize(1)
        scraperTrigger.scrape(7075L)
        Measurements.all() must haveSize(2)
      }
    }
  }

}
