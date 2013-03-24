package net.surguy.floodtweet

import org.specs2.mutable.Specification

class ScraperTriggerTest extends Specification {

  "scraping a river" should {
    "send a tweet" in { (ScraperTrigger.scrape("7075") must not).throwAn[Exception]()  }
  }

}
