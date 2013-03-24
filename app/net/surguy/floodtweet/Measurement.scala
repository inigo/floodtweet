package net.surguy.floodtweet

import org.joda.time.DateTime

/**
 * An individual river level measurement.
 */
case class Measurement(stationId: String, takenAt: DateTime, currentLevel: Double, typicalLow: Double, typicalHigh: Double)
