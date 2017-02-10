package holmes.ponderosa.lights

import java.time.LocalTime

/**
 * A TimeFrame is a description of a light level. These are stacked togeher to
 * represent how we want to light up at a certain time of day.
 */
data class TimeFrame(val endTime: LocalTime, val lowLevel: Int, val highLevel: Int) {
  fun contains(currentTime: LocalTime): Boolean {
    return endTime.isAfter(currentTime)
  }
}
