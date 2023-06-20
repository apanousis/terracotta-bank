package gatling.test.example.simulation

import gatling.test.example.simulation.PerfTestConfig.{baseUrl, durationMin, maxResponseTimeMs, meanResponseTimeMs}
import io.gatling.core.Predef.{constantUsersPerSec, global, scenario, _}
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef.{http, status, _}
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._
import scala.language.postfixOps

class ExampleGetSimulation extends Simulation {
  val httpConf: HttpProtocolBuilder = http.baseUrl(baseUrl)
  val getUsers: ScenarioBuilder = scenario("Root end point calls")
    .exec(http("root end point")
      .get("")
      .check(status.is(200))
    )
  setUp(getUsers.inject(
    constantUsersPerSec(PerfTestConfig.requestPerSecond) during (durationMin minutes))
    .protocols(httpConf))
    .assertions(
      global.responseTime.max.lt(meanResponseTimeMs),
      global.responseTime.mean.lt(maxResponseTimeMs),
      global.successfulRequests.percent.gt(95)
    )
}
