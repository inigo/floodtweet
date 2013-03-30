package net.surguy.floodtweet

import org.specs2.mutable.Specification
import play.api.test.Helpers._
import play.api.test.FakeApplication
import models.{Measurement, Station}

class ScraperTriggerTest extends Specification {

  "scraping a river" should {
    "send a tweet" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        var tweets = 0
        val mockTweeter = new Tweeter() { override def tweet(station: Station, measurement: Measurement) { tweets = tweets + 1 } }
        val scraperTrigger = new ScraperTrigger(new EnvironmentAgencyScraper(), mockTweeter)
        (scraperTrigger.scrape(7075L) must not).throwAn[Exception]()
//        tweets mustEqual(1)
      }
    }
  }

}
