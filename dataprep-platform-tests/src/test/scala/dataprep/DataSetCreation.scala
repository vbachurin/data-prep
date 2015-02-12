package dataprep

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class DataSetCreation extends Simulation {
  val httpConf = http
    .warmUp("http://localhost:8888/")
    .baseURL("http://localhost:8888/")
    .acceptHeader("application/json,application/xml;q=0.9,*/*;q=0.8") // Here are the common headers
    .doNotTrackHeader("1")
  val scn = scenario("Create / Read / No Op Transform")
    .during(1 minute) {
    group("actions") {
      exec(http("creation").post("api/datasets/").body(RawFileBody("data/test1.csv")).check(bodyString.saveAs("dataset")))
        .exec(http("read_1").get("api/datasets/${dataset}").check(status.saveAs("dataset_get_status")))
        .asLongAs(session => session.get("dataset_get_status").as[Integer].equals(202)) {
        exec(http("read_2").get("api/datasets/${dataset}").check(status.saveAs("dataset_get_status")))
          .pause(500 millis)
        }
        .exec(http("transform").post("api/transform/${dataset}/"))
        .pause(500 millis)
    }
  }
  setUp(scn.inject(rampUsers(100) over (30 seconds))).protocols(httpConf)
}
