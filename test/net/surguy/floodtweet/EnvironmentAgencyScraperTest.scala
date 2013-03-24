package net.surguy.floodtweet

import org.specs2.mutable.Specification
import java.util.Date
import org.joda.time.DateTime
import net.surguy.floodtweet.{Station, Measurement, EnvironmentAgencyScraper}

class EnvironmentAgencyScraperTest extends Specification {
  val scraper = new EnvironmentAgencyScraper()
  val id = "7075"

  "retrieving data for Bulstake" should {
    "identify the station" in { scraper.scrapeLevel(id)._1 mustEqual(Station(id, "New Botley", "Bulstake Stream"))  }
    // This needs to be working against a saved file
    "identify the measurement" in {
      scraper.scrapeLevel(id)._2 mustEqual(Measurement(id, new DateTime(2013, 3, 24, 9, 0), 2.12D, 0.87D, 1.95D))
    }
  }

}
