package holmes.ponderosa.lights

import java.time.LocalTime
import javax.inject.Provider

class LightsRequest(val twilight: Twilight, val zones: LightZones, val now: Provider<LocalTime>) {
  fun autoDim(deviceId: Int, currentValue: Int): DimSchedule.AutoDimResult {
    return DimSchedule(now).autoDim(lightZone(deviceId), currentValue)
  }

  fun toggleLights(deviceId: Int, currentValue: Int): DimSchedule.ToggleLightResult {
    return DimSchedule(now).toggleLights(lightZone(deviceId), currentValue)
  }

  private fun lightZone(deviceId: Int): LightZone {
    return zones.zone(deviceId)
  }
}
