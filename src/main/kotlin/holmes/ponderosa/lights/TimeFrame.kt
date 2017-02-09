package holmes.ponderosa.lights

import java.util.Date

/**
 * A TimeFrame is a description of a light level. These are stacked togeher to
 * represent how we want to light up at a certain time of day.
 */
data class TimeFrame(val endTime: Date, val lowLevel: Int, val highLevel: Int) {
  fun contains(date: Date): Boolean {
    return endTime.after(date)
  }
}
