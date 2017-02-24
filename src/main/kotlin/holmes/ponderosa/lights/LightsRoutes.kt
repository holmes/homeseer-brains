package holmes.ponderosa.lights

import holmes.ponderosa.lights.DimCalculator.AutoDimResult.Factory.NO_CHANGE
import holmes.ponderosa.lights.DimCalculator.ToggleLightResult
import spark.Request
import spark.Route
import spark.Spark.get
import spark.Spark.halt
import spark.Spark.path

class LightsRoutes(val zones: LightZones, val dimCalculator: DimCalculator, val lightsTransformer: LightsTransformer) {
  fun initialize() {
    path("/api/lights/:deviceId") {

      get("/autoDim/:currentValue", Route { request, _ ->
        val zone = request.zone() ?: return@Route NO_CHANGE
        val currentValue = request.params("currentValue")?.toInt()

        return@Route when (currentValue) {
          null -> NO_CHANGE
          else -> dimCalculator.autoDim(zone, currentValue)
        }
      }, lightsTransformer)

      get("/toggle/:currentValue", Route { request, _ ->
        val zone = request.zone() ?: return@Route NO_CHANGE
        val currentValue = request.params("currentValue")?.toInt()

        return@Route when (currentValue) {
          null -> ToggleLightResult(emptyList())
          else -> dimCalculator.toggleLights(zone, currentValue)
        }
      }, lightsTransformer)
    }
  }

  private fun Request.zone(): LightZone? {
    val zoneId = params("deviceId").toInt()
    val zone = zoneId.let { zones.zone(zoneId) }

    return if (zone != null) zone else {
      halt(400, "Bad zoneId")
      null
    }
  }
}
