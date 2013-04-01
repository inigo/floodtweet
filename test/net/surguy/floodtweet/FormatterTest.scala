package net.surguy.floodtweet

import org.specs2.mutable.Specification
import models.{Measurement, Station}
import org.joda.time.DateTime

class FormatterTest extends Specification {
  private val formatter = new Formatter()
  private val testStation = new Station(1L, "Test", "A river")
  private def fmt(measurements: Seq[Measurement]): String = formatter.formatMessage(testStation, measurements)
  private def meas(level: Double): Measurement = Measurement("guid", 1L, DateTime.now, level, 1.0D, 2.0D)

  "formatting no measurements" should {
    "include the station name" in { fmt(List()) must contain("Test") }
  }

  "formatting a single measurement" should {
    "report a high river" in { fmt(List(meas(2.1))) must contain("RIVER HIGH")}
    "report a very high river" in { fmt(List(meas(3.0))) must contain("RIVER VERY HIGH")}
    "contain the river level" in { fmt(List(meas(2.1))) must contain("2.1 m")}
    "cope with zeroes" in { fmt(List(Measurement("guid", 1L, DateTime.now, 1.2, 0, 0))) must contain("1.2 m")}
  }

  "formatting two measurements" should {
    "report a high river based on the last measurement" in { fmt(List(meas(1.0), meas(2.1))) must contain("RIVER HIGH")}
    "contain the current level" in { fmt(List(meas(1.0), meas(2.1))) must contain("2.1 m")}
    "contain the previous level" in { fmt(List(meas(1.0), meas(2.1))) must contain("1.0 m")}
    "indicate the level is rising" in { fmt(List(meas(1.0), meas(2.1))) must contain("↑")}
    "indicate the level is falling" in { fmt(List(meas(2.2), meas(2.1))) must contain("↓")}
    "indicate the level is stable" in { fmt(List(meas(2.2), meas(2.2))) must contain("stable")}
  }

  "formatting three measurements" should {
    "report a high river based on the last measurement" in { fmt(List(meas(1.0), meas(2.1), meas(2.2))) must contain("RIVER HIGH")}
    "contain the current level" in { fmt(List(meas(1.0), meas(2.1), meas(2.2))) must contain("2.2 m")}
    "contain the previous level" in { fmt(List(meas(1.0), meas(2.1), meas(2.2))) must contain("2.1 m")}
    "contain a sparkline" in { fmt(List(meas(1.0), meas(2.5), meas(3.0))) must contain("▁▄█")}
  }

  "calculating height percentages" should {
    "return subzero for levels in the typical band" in { formatter.percentageAboveNormal(meas(1.0)) must beLessThan(0.0) }
    "return greater than 1 for very very high levels" in { formatter.percentageAboveNormal(meas(5.0)) must beGreaterThan(1.0) }
    "return expected values for intermediate levels" in { formatter.percentageAboveNormal(meas(2.5)) mustEqual(0.5) }
  }

  "creating sparklines" should {
    "return ▁ for low values" in { formatter.toBlock(-1.0) mustEqual("▁") }
    "return █ for high values" in { formatter.toBlock(2.0) mustEqual("█") }
    "return ▄ for halfway values" in { formatter.toBlock(0.5) mustEqual("▄") }
  }

}
