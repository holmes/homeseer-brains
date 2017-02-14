package holmes.ponderosa.lights

import com.squareup.moshi.Moshi
import spark.Spark.get
import spark.Spark.halt
import spark.Spark.path
import java.time.LocalDate
import java.time.LocalTime

class LightsRoutes {
  // Getting dagger to work is just too hard. Doing it by hand for now.
  val moshi = Moshi.Builder().build()
  val today = { LocalDate.now() }
  val now = { LocalTime.now() }
  val twilightProvider = TwilightProvider(moshi, today)::twilight
  val twilight = Twilight(twilightProvider)
  val lightsRequest = LightsRequest(twilight, now)

  fun initialize() {
    path("/api/lights/:deviceId") {

      get("/autoDim/:currentValue") { request, response ->
        val deviceId = request.params("deviceId")?.toInt()
        if (deviceId == null) {
          halt(400, "Bad deviceId")
          return@get ""
        }

        val currentValue = request.params("currentValue")?.toInt()

        val autoDimResult = when (currentValue) {
          null -> DimSchedule.AutoDimResult.NO_CHANGE
          else -> lightsRequest.autoDim(deviceId, currentValue)
        }

        return@get "${autoDimResult.dimLevel},${autoDimResult.needsReschedule}"
      }

      get("/toggle/:currentValue") { request, _ ->
        val deviceId = request.params("deviceId")?.toInt()
        if (deviceId == null) {
          halt(400, "Bad deviceId")
          return@get ""
        }

        val currentValue = request.params("currentValue")?.toInt()

        val dimResult = when (currentValue) {
          null -> DimSchedule.AutoDimResult.NO_CHANGE
          else -> lightsRequest.toggleLights(deviceId, currentValue)
        }

        return@get "$dimResult"
      }
    }
  }
}
