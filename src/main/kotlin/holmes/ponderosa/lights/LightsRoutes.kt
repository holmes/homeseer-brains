package holmes.ponderosa.lights

import spark.Route
import spark.Spark.get
import spark.Spark.halt
import spark.Spark.path

class LightsRoutes(val lightsRequest: LightsRequest, val toggleLightResultTransformer: ToggleLightResultTransformer) {

  fun initialize() {
    path("/api/lights/:deviceId") {

      get("/autoDim/:currentValue") { request, _ ->
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

        "${autoDimResult.dimLevel},${autoDimResult.needsReschedule}"
      }

      get("/toggle/:currentValue", Route { request, _ ->
        val deviceId = request.params("deviceId")?.toInt()
        if (deviceId == null) {
          halt(400, "Bad deviceId")
          return@Route null
        }

        val currentValue = request.params("currentValue")?.toInt()

        val dimResult = when (currentValue) {
          null -> DimSchedule.ToggleLightResult(setOf())
          else -> lightsRequest.toggleLights(deviceId, currentValue)
        }

        dimResult.toString()
      }, toggleLightResultTransformer)
    }
  }
}
