package holmes.ponderosa.lights

import java.time.LocalTime

data class DimSchedule(val deviceId: Int, val timeFrames: List<TimeFrame>, val now: () -> LocalTime) {
  fun autoDim(currentValue: Int): Int {
    if (currentValue == 0) {
      return -1
    }

    if (isInFirstFrame) {
      return timeFrames.first().lowLevel
    }

    return -1
  }

  fun toggleLights(currentValue: Int): Int {
    return -1
  }


  private val isInFirstFrame: Boolean
  get() = timeFrames.get(0).contains(now())
}
