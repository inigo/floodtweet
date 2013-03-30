package net.surguy.floodtweet

import org.specs2.mutable.Specification
import org.joda.time.DateTime
import models.{Measurement, Guid, Station}

class EnvironmentAgencyScraperTest extends Specification {
  val scraper = new EnvironmentAgencyScraper()
  val id = 7075L

  "retrieving data for Bulstake" should {
    "identify the station" in { scraper.scrapeLevel(id)._1.get mustEqual(Station(id, "New Botley", "Bulstake Stream"))  }
    // This needs to be working against a saved file
    "identify the measurement" in {
      scraper.scrapeLevel(id)._2.get mustEqual(Measurement(Guid.next, id, new DateTime(2013, 3, 24, 9, 0), 2.12D, 0.87D, 1.95D))
    }
  }

}
