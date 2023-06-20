package gatling.test.example.simulation

import gatling.test.example.simulation.PerfTestConfig.{baseUrl, durationMin, maxResponseTimeMs, meanResponseTimeMs}
import io.gatling.core.Predef.{constantUsersPerSec, global, scenario, _}
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef.{http, status, _}
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._
import scala.language.postfixOps

class ExamplePostSimulation extends Simulation {
  val httpConf: HttpProtocolBuilder = http.baseUrl(baseUrl)
  val postUsers: ScenarioBuilder = scenario("Root endpoint calls")
    .exec(http("home page")
      .get("/")
      .check(css("#login-csrf-token","value").saveAs("csrfToken")))
    .pause(2)
    .exec(http("login end point")
      .post("/login")
      .formParam("username", "admin")
      .formParam("password", "admin")
      .formParam("relay", "")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("login", "LOGIN")
      .check(status.is(200))
    )
  setUp(postUsers.inject(
    constantUsersPerSec(PerfTestConfig.requestPerSecond) during (durationMin minutes))
    .protocols(httpConf))
    .assertions(
      global.responseTime.max.lt(meanResponseTimeMs),
      global.responseTime.mean.lt(maxResponseTimeMs),
      global.successfulRequests.percent.gt(95)
    )
}
