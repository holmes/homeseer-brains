package holmes.ponderosa

import com.squareup.moshi.Moshi
import holmes.ponderosa.lights.DimSchedule
import holmes.ponderosa.lights.LightsRequest
import holmes.ponderosa.lights.Twilight
import holmes.ponderosa.lights.TwilightProvider
import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.application.ApplicationEnvironment
import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.route
import org.jetbrains.ktor.routing.routing
import java.time.LocalDate
import java.time.LocalTime

class HomeSeerBrains(environment: ApplicationEnvironment) : Application(environment) {
  init {
    routing {
      route("/lights/{deviceId}") {
        // Getting dagger to work is just too hard. Doing it by hand for now.
        val moshi = Moshi.Builder().build()
        val today = { LocalDate.now() }
        val now = { LocalTime.now() }
        val twilightProvider = TwilightProvider(moshi, today)::twilight
        val twilight = Twilight(twilightProvider)

        val lightsRequest = LightsRequest(twilight, now)

        get("/autoDim/{currentValue}") {
          val deviceId = call.parameters["deviceId"]?.toInt() ?: throw IllegalArgumentException("Unknown deviceId")
          val currentValue = call.parameters["currentValue"]?.toInt()

          val autoDimResult = when (currentValue) {
            null -> DimSchedule.AutoDimResult.NO_CHANGE
            else -> lightsRequest.autoDim(deviceId, currentValue)
          }

          call.respondText(ContentType.Text.Plain, "${autoDimResult.dimLevel},${autoDimResult.needsReschedule}")
        }

        get("/toggle/{currentValue}") {
          val deviceId = call.parameters["deviceId"]?.toInt() ?: throw IllegalArgumentException("Unknown deviceId")
          val currentValue = call.parameters["currentValue"]?.toInt()

          val result = when (currentValue) {
            null -> -1
            else -> lightsRequest.toggleLights(deviceId, currentValue)
          }

          call.respondText(ContentType.Text.Plain, result.toString())
        }
      }
    }
  }
}
