package holmes.ponderosa.lights

import java.time.LocalTime
import javax.inject.Provider

class LightsRequest(val twilight: Twilight, val now: Provider<LocalTime>) {
  fun autoDim(deviceId: Int, currentValue: Int): DimSchedule.AutoDimResult {
    return getDimSchedule(deviceId).autoDim(currentValue)
  }

  fun toggleLights(deviceId: Int, currentValue: Int): DimSchedule.ToggleLightResult {
    return getDimSchedule(deviceId).toggleLights(currentValue)
  }

  fun getDimSchedule(deviceId: Int): DimSchedule {
    val timeFrames: List<TimeFrame>

    when (deviceId) {
      22 -> { // Family Room
        timeFrames = listOf(
            TimeFrame(twilight.twilightBegin(), 1, 35),
            TimeFrame(twilight.sunrise(), 1, 35),
            TimeFrame(twilight.solarNoon(), 30, 85),
            TimeFrame(twilight.twilightEnd(), 10, 60),
            TimeFrame(LocalTime.MAX, 1, 35)
        )
      }
      28 -> { // Nursery
        timeFrames = listOf(
            TimeFrame(twilight.twilightBegin(), 1, 35),
            TimeFrame(twilight.sunrise(), 1, 35),
            TimeFrame(twilight.solarNoon(), 30, 85),
            TimeFrame(LocalTime.MAX, 1, 35)
        )
      }
      25 -> { // Guest Bath
        timeFrames = listOf(
            TimeFrame(twilight.twilightBegin(), 5, 35),
            TimeFrame(twilight.solarNoon(), 35, 85),
            TimeFrame(twilight.twilightEnd(), 20, 60),
            TimeFrame(LocalTime.of(23, 59), 5, 35)
        )
      }
      19 -> { // Kitchen
        timeFrames = listOf(
            TimeFrame(twilight.twilightBegin(), 10, 35),
            TimeFrame(twilight.solarNoon(), 35, 85),
            TimeFrame(twilight.twilightEnd(), 25, 60),
            TimeFrame(LocalTime.of(23, 59), 18, 35)
        )
      }
      15 -> { // Dining Room
        timeFrames = listOf(
            TimeFrame(twilight.twilightBegin(), 5, 35),
            TimeFrame(twilight.solarNoon(), 30, 85),
            TimeFrame(twilight.twilightEnd(), 15, 60),
            TimeFrame(LocalTime.of(23, 59), 5, 35)
        )
      }
      else -> {
        throw IllegalArgumentException("Unknown deviceId: $deviceId")
      }
    }

    return DimSchedule(deviceId, timeFrames, now)
  }
}
