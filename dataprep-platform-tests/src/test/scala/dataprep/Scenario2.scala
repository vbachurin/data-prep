package dataprep

import io.gatling.core.Predef._
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
      exec(http("creation").post("api/datasets/").header("Content-Type", "text/csv").body(RawFileBody("data/test1.csv")).check(bodyString.saveAs("dataset")))
        .exec(http("read_1").get("api/datasets/${dataset}"))
        .exec(http("transform").post("api/transform/${dataset}").header("content-type", "application/json").body(StringBody("{\"actions\": [{\"action\": \"uppercase\",\"parameters\": {\"column_id\": \"0001\", \"scope\": \"column\"}}]}")))
    }
  }
  setUp(scn.inject(rampUsers(100) over (30 seconds))).protocols(httpConf)
}
