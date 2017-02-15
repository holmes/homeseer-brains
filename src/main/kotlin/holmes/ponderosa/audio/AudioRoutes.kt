package holmes.ponderosa.lights

import com.squareup.moshi.Moshi
import spark.Spark.halt
import spark.Spark.path
import spark.Spark.post
import java.time.LocalDate
import java.time.LocalTime

class AudioRoutes {
  // Getting dagger to work is just too hard. Doing it by hand for now.
  val moshi = Moshi.Builder().build()
  val today = { LocalDate.now() }
  val now = { LocalTime.now() }
  val twilightProvider = TwilightProvider(moshi, today)::twilight
  val twilight = Twilight(twilightProvider)
  val lightsRequest = LightsRequest(twilight, now)

  fun initialize() {
    path("/api/audio/:zoneId") {

      post("/power/:newValue") { request, _ ->
        val deviceId = request.params("deviceId")?.toInt()
        if (deviceId == null) {
          halt(400, "Bad deviceId")
          return@post ""
        }

        val currentValue = request.params("currentValue")?.toInt()

        val autoDimResult = when (currentValue) {
          null -> DimSchedule.AutoDimResult.NO_CHANGE
          else -> lightsRequest.autoDim(deviceId, currentValue)
        }

        return@post "${autoDimResult.dimLevel},${autoDimResult.needsReschedule}"
      }

      post("/source/:sourceId") { request, _ ->
        val deviceId = request.params("deviceId")?.toInt()
        if (deviceId == null) {
          halt(400, "Bad deviceId")
          return@post ""
        }

        val currentValue = request.params("currentValue")?.toInt()

        val dimResult = when (currentValue) {
          null -> -1
          else -> lightsRequest.toggleLights(deviceId, currentValue)
        }

        return@post "$dimResult"
      }
    }
  }
}
