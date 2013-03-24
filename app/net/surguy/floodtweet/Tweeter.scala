package net.surguy.floodtweet

import twitter4j.TwitterFactory

/**
 * Tweets a river level message.
 *
 * @author Inigo Surguy
 */
class Tweeter {

  def tweet(station: Station, measurement: Measurement) {
    val msg = formatMessage(station, measurement)
    sendTweet(msg)
  }

  private def formatMessage(station: Station, measurement: Measurement) = {
    measurement match {
      case _ if measurement.currentLevel > (measurement.typicalHigh*1.5) =>
        "RIVER VERY HIGH at %s - at %s m compared to typical high of %s m".format(station.name, measurement.currentLevel, measurement.typicalHigh)
      case _ if measurement.currentLevel > measurement.typicalHigh =>
        "RIVER HIGH at %s - at %s m compared to typical high of %s m".format(station.name, measurement.currentLevel, measurement.typicalHigh)
      case _ => "River level at %s is at %s m".format(station.name, measurement.currentLevel)
    }
  }

  private def sendTweet(msg: String) {
    val twitter = TwitterFactory.getSingleton
    twitter.updateStatus(msg)
  }

}
