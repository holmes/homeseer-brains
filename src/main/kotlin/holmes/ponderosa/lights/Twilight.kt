package holmes.ponderosa.lights

import java.util.Calendar
import java.util.Date

/**
 * Finds sunrise/sunset and solar noon for use in [TimeFrame]s.
 */
class Twilight(val resultsProvider: (() -> TwilightResult)) {
  fun sunrise(offset: Int = 0): Date {
    return addOffset(offset, resultsProvider().sunrise)
  }

  fun sunset(offset: Int = 0): Date {
    return addOffset(offset, resultsProvider().sunset)
  }

  fun solarNoon(offset: Int = 0): Date {
    return addOffset(offset, resultsProvider().solarNoon)
  }

  private fun addOffset(offset: Int, baseDate: Date): Date {
    val cal = Calendar.getInstance()
    cal.time = baseDate
    cal.add(Calendar.MINUTE, offset)
    return cal.time
  }
}

data class TwilightResult(val sunrise: Date, val sunset: Date, val solarNoon: Date)
