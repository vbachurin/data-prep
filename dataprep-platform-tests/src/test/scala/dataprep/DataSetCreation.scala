package dataprep

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class DataSetCreation extends Simulation {
  val httpConf = http
    .baseURL("http://localhost:8888/")
    .acceptHeader("application/json,application/xml;q=0.9,*/*;q=0.8") // Here are the common headers
    .doNotTrackHeader("1")
  val scn = scenario("Create / Read / No Op Transform")
    .during(1 minute) {
    group("actions") {
      exec(http("creation").post("api/datasets/").body(RawFileBody("data/test1.csv")).check(bodyString.find.saveAs("dataset"))).exec(http("read").get("api/datasets/${dataset}"))
        .exec(http("transform").post("api/transform/${dataset}/"))
        .pause(500 millis)
    }
  }
  setUp(scn.inject(rampUsers(30) over (30 seconds))).protocols(httpConf)
}
