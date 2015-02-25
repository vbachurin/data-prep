package dataprep

import io.gatling.core.Predef._
import io.gatling.core.validation.Validation
import io.gatling.http.Predef._

import scala.concurrent.duration._

class Scenario2 extends Simulation {
  val httpConf = http
    .warmUp("http://localhost:8888/")
    .baseURL("http://localhost:8888/")
    .shareConnections
    .disableCaching
    .acceptHeader("application/json,application/xml;q=0.9,*/*;q=0.8") // Here are the common headers
    .doNotTrackHeader("1")

  val scn = scenario("Create / Read / Transform Upper Case")
    .during(1 minute) {
    group("actions") {
      exec(http("creation").post("api/datasets/").body(RawFileBody("data/test1.csv")).check(bodyString.saveAs("dataset")))
        .exec(http("read_1").get("api/datasets/${dataset}").check(status.saveAs("dataset_get_status")))
        .asLongAs(session => session.get("dataset_get_status").as[Integer].equals(202)) {
        exec(http("read_2").get("api/datasets/${dataset}").check(status.saveAs("dataset_get_status")))
          .pause(500 millis)
        }
        .exec(http("transform").post("api/transform/${dataset}").header("content-type", "application/json").body(StringBody("{\"actions\": [{\"action\": \"uppercase\",\"parameters\": {\"column_name\": \"lastname\"}}]}")))
        .pause(500 millis)
    }
  }
  setUp(scn.inject(rampUsers(100) over (30 seconds))).protocols(httpConf)
}
