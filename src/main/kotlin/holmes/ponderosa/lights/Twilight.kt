package holmes.ponderosa.lights

import java.time.LocalTime

/**
 * Finds sunrise/sunset and solar noon for use in [TimeFrame]s.
 */
class Twilight(val resultsProvider: (() -> TwilightResult)) {
  fun sunrise(offset: Int = 0): LocalTime {
    return addOffset(offset, resultsProvider().sunrise)
  }

  fun sunset(offset: Int = 0): LocalTime {
    return addOffset(offset, resultsProvider().sunset)
  }

  fun solarNoon(offset: Int = 0): LocalTime {
    return addOffset(offset, resultsProvider().solarNoon)
  }

  private fun addOffset(offset: Int, baseDate: LocalTime): LocalTime {
    return baseDate.plusMinutes(offset.toLong())
  }
}

data class TwilightResult(val sunrise: LocalTime, val sunset: LocalTime, val solarNoon: LocalTime)
