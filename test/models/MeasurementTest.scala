package models

import play.api.test.Helpers._
import play.api.test.FakeApplication
import org.joda.time.DateTime
import org.specs2.mutable.Specification
import java.util.UUID

class MeasurementTest extends Specification {
  sequential

  "creating measurements" should {
    "store and retrieve measurements" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        Stations.create(Station(100, "Test", "Test River"))
        Measurements.create(Measurement(Guid.next, 100L, DateTime.now(), 2.5D, 1.0D, 2.0D))
        Measurements.allForStation(100L) must haveLength(1)
      }
    }
  }

}
