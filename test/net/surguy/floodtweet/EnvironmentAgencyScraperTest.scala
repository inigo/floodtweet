package net.surguy.floodtweet

import org.specs2.mutable.Specification
import org.joda.time.DateTime
import models.{Measurement, Station}
import java.io.File

class EnvironmentAgencyScraperTest extends Specification {
  val scraper = new EnvironmentAgencyScraper()
  val id = 7075L

  "retrieving data for Bulstake" should {
    "identify the station reading from the server" in { scraper.scrapeLevel(id)._1.get mustEqual(Station(id, "New Botley", "Bulstake Stream"))  }
    "identify the measurement reading from a local file" in {
      val url = new File("test/bulstake_test.html").toURI.toString
      scraper.driver.setJavascriptEnabled(false)
      scraper.driver.get(url)
      val m = scraper.retrieveMeasurementFromPage(id)
      m mustEqual(Measurement(m.guid, id, new DateTime(2013, 3, 24, 9, 0), 2.12D, 0.87D, 1.95D))
    }
  }

}
