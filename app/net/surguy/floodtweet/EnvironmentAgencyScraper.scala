package net.surguy.floodtweet

import com.gargoylesoftware.htmlunit.BrowserVersion
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.By
import org.joda.time.format.DateTimeFormat
import models.{Guid, Measurement, Station}

/**
 * Retrieve measurement and station information from the Environment Agency River Levels website
 * at http://www.environment-agency.gov.uk/homeandleisure/floods/riverlevels/
 */
class EnvironmentAgencyScraper extends Logging {

  private[floodtweet] val driver = new HtmlUnitDriver(BrowserVersion.FIREFOX_10)
  driver.setJavascriptEnabled(true)

  private val baseStationUrl = "http://www.environment-agency.gov.uk/homeandleisure/floods/riverlevels/136495.aspx?stationId=%s"

  def scrapeLevel(stationId: Long): (Option[Station], Option[Measurement]) = {
    try {
      val url = baseStationUrl.format(stationId)
      log.debug("Scraping info from "+url)
      driver.get(url)
      Thread.sleep(1000) // Not sure why this is needed - but the station data is not present in the driver without a pause
      val station = retrieveStationFromPage(stationId)
      val measurement = retrieveMeasurementFromPage(stationId)
      (Some(station), Some(measurement))
    }
    catch {
      case e: Exception =>
        log.warn("Unable to scrape for station " + stationId + " : " + e, e)
        (None, None)
    }
  }

  private[floodtweet] def retrieveStationFromPage(stationId: Long) = {
    val stationDataEl = driver.findElement(By.xpath("//div[h2='Station data']"))
    val name = extractValue(stationDataEl.findElement(By.xpath("ul/li[1]")).getText)
    val watercourse = extractValue(stationDataEl.findElement(By.xpath("ul/li[3]")).getText)
    Station(stationId, name, watercourse)
  }

  private[floodtweet] def retrieveMeasurementFromPage(stationId: Long) = {
    val currentLevelText = extractValue(driver.findElement(By.xpath("//div[@class='chart-top']/h3")).getText)
    val currentLevel = currentLevelText.replaceAll("m","").toDouble

    val textInfoEl = driver.findElement(By.xpath("//div[h2='Summary']/div[@class='plain_text']"))

    // e.g. "This measurement was recorded at 09:00 on 24/03/2013."
    val takenAtText = textInfoEl.findElement(By.xpath("p[2]")).getText
    val dateTimeText = takenAtText.replaceAll(".*?at (\\d+):(\\d+) on (\\d+)/(\\d+)/(\\d+)\\.*", "$5-$4-$3 $1:$2")
    val takenAt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(dateTimeText)

    // e.g. "The typical river level range for this location is between 0.87 metres and 1.95 metres."
    val typicalLevelText = textInfoEl.findElement(By.xpath("p[3]")).getText
    val typicalLow = typicalLevelText.replaceAll(".*? between ([\\d+\\.]+).*", "$1").toDouble
    val typicalHigh = typicalLevelText.replaceAll(".*? and ([\\d+\\.]+).*", "$1").toDouble

    Measurement(Guid.next, stationId, takenAt, currentLevel, typicalLow, typicalHigh)
  }

  private def extractValue(s: String) =
    if (s.contains(":")) s.substring(s.indexOf(":")+1).trim else throw new IllegalArgumentException("Unexpected value: "+s)


}



