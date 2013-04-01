package net.surguy.floodtweet

import twitter4j.TwitterFactory
import models.{Station, Measurement}

/**
 * Tweets a river level message.
 *
 * @author Inigo Surguy
 */
class Tweeter(formatter: Formatter) extends Logging {

  def tweet(station: Station, measurements: Seq[Measurement]) {
    val msg = formatter.formatMessage(station, measurements)
    val twitter = TwitterFactory.getSingleton
    log.info("Sending tweet : "+msg)
    twitter.updateStatus(msg)
  }

}
