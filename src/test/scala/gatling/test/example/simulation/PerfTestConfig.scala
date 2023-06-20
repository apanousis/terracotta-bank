package gatling.test.example.simulation

import gatling.test.example.simulation.SystemPropertiesUtil._

object PerfTestConfig {
  val baseUrl: String = getAsStringOrElse("baseUrl", "http://localhost:8080")
  val requestPerSecond: Double = getAsDoubleOrElse("requestPerSecond", 10f)
  val durationMin: Double = getAsDoubleOrElse("durationMin", 1.0)
  val meanResponseTimeMs: Int = getAsIntOrElse("meanResponseTimeMs", 500)
  val maxResponseTimeMs: Int = getAsIntOrElse("maxResponseTimeMs", 1000)
}
