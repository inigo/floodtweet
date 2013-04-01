package net.surguy.floodtweet

import models.{Measurement, Station}

/**
 * Converts measurements into a message.
 *
 * @author Inigo Surguy
 */
class Formatter {

  /** Expects a sequence of measurements ordered with the newest last in the list. */
  def formatMessage(station: Station, measurements: Seq[Measurement]): String = {
    measurements.size match {
      case 0 => "No information for %s".format(station.name)
      case 1 => messageText(station, measurements.last, None)
      case 2 => messageText(station, measurements.last, Some(measurements(measurements.size - 2)))
      case _ => messageText(station, measurements.last, Some(measurements(measurements.size - 2))) + " " +formatLevelGraph(measurements)
    }
  }

  def messageText(station: Station, m: Measurement, previousOpt: Option[Measurement]) = {
    (m, previousOpt) match {
      case (measurement, Some(previous)) if measurement.level >= (measurement.typicalHigh*1.5) && measurement.level > previous.level =>
        "RIVER VERY HIGH AND RISING at %s - at %s m (↑ from %s m) compared to typical high of %s m".format(station.name, measurement.level, previous.level, measurement.typicalHigh)
      case (measurement, Some(previous)) if measurement.level >= (measurement.typicalHigh*1.5) && measurement.level == previous.level =>
        "RIVER VERY HIGH at %s - %s m (stable) compared to typical high of %s m".format(station.name, measurement.level, measurement.typicalHigh)
      case (measurement, Some(previous)) if measurement.level >= (measurement.typicalHigh*1.5) && measurement.level < previous.level =>
        "RIVER VERY HIGH at %s - %s m (↓ from %s m) compared to typical high of %s m".format(station.name, measurement.level, previous.level, measurement.typicalHigh)
      case (measurement, None) if measurement.level >= (measurement.typicalHigh*1.5) =>
        "RIVER VERY HIGH at %s - %s m compared to typical high of %s m".format(station.name, measurement.level, measurement.typicalHigh)
      case (measurement, Some(previous)) if measurement.level > measurement.typicalHigh && measurement.level > previous.level =>
        "RIVER HIGH at %s - %s m (↑ from %s m) compared to typical high of %s m".format(station.name, measurement.level, previous.level, measurement.typicalHigh)
      case (measurement, Some(previous)) if measurement.level > measurement.typicalHigh && measurement.level == previous.level =>
        "RIVER HIGH at %s - %s m (stable) compared to typical high of %s m".format(station.name, measurement.level, measurement.typicalHigh)
      case (measurement, Some(previous)) if measurement.level > measurement.typicalHigh && measurement.level < previous.level =>
        "RIVER HIGH at %s - %s m (↓ from %s m) compared to typical high of %s m".format(station.name, measurement.level, previous.level, measurement.typicalHigh)
      case (measurement, None) if measurement.level > measurement.typicalHigh =>
        "RIVER HIGH at %s - at %s m compared to typical high of %s m".format(station.name, measurement.level, measurement.typicalHigh)
      case _ => "River level at %s is at %s m".format(station.name, m.level)
    }
  }

  def formatLevelGraph(measurements: Seq[Measurement]): String = measurements.map(percentageAboveNormal).map(toBlock).mkString
  private[floodtweet] def percentageAboveNormal(m: Measurement): Double = (m.level - m.typicalHigh) / (m.typicalHigh - m.typicalLow)

  private[floodtweet] def toBlock(percentage: Double) = percentage match {
    case _ if percentage <= (1.0/8.0) => "▁"
    case _ if percentage <= (2.0/8.0) => "▂"
    case _ if percentage <= (3.0/8.0) => "▃"
    case _ if percentage <= (4.0/8.0) => "▄"
    case _ if percentage <= (5.0/8.0) => "▅"
    case _ if percentage <= (6.0/8.0) => "▆"
    case _ if percentage <= (7.0/8.0) => "▇"
    case _ => "█"
  }

}
