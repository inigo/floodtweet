package models

import org.specs2.mutable.Specification
import play.api.test.Helpers._
import play.api.test.FakeApplication
import org.joda.time.DateTime

class MeasurementsTest extends Specification {
  val stationId: Long = 100L
  val now = DateTime.now

  "retrieving measurements" should {
    "get the last measurement" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        createDummyData()
        Measurements.lastMeasurementFor(stationId).get.level mustEqual(1.3)
      }
    }
    "retrieve nothing when there are no measurements" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        createDummyData()
        Measurements.lastMeasurementFor(999L) must beNone
      }
    }
    "retrieve a list of measurements with the most recent last" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        createDummyData()
        Measurements.lastN(stationId, 2).map(_.level) mustEqual List(1.2, 1.3)
      }
    }
    "retrieve as many measurements as are present if trying to retrieve too many" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        createDummyData()
        Measurements.lastN(stationId, 10).map(_.level) mustEqual List(1.1, 1.2, 1.3)
      }
    }
  }

  def createDummyData() {
    Stations.create(new Station(stationId, "Test", "River"))
    val measurements = List(Measurement(Guid.next, stationId, now, 1.3, 1.0, 2.0)
      , Measurement(Guid.next, stationId, now.minusHours(1), 1.2, 1.0, 2.0)
      , Measurement(Guid.next, stationId, now.minusHours(2), 1.1, 1.0, 2.0))
    measurements.map(Measurements.create)
  }

}
