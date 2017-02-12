package holmes.ponderosa.lights

import java.time.LocalTime

/**
 * Finds sunrise/sunset and solar noon for use in [TimeFrame]s.
 */
class Twilight(val resultsProvider: (() -> TwilightResult)) {
  fun twilightBegin(offset: Int = 0): LocalTime {
    return addOffset(offset, resultsProvider().twilightBegin)
  }

  fun sunrise(offset: Int = 0): LocalTime {
    return addOffset(offset, resultsProvider().sunrise)
  }

  fun sunset(offset: Int = 0): LocalTime {
    return addOffset(offset, resultsProvider().sunset)
  }

  fun solarNoon(offset: Int = 0): LocalTime {
    return addOffset(offset, resultsProvider().solarNoon)
  }

  fun twilightEnd(offset: Int = 0): LocalTime {
    return addOffset(offset, resultsProvider().twilightEnd)
  }

  private fun addOffset(offset: Int, baseDate: LocalTime): LocalTime {
    return baseDate.plusMinutes(offset.toLong())
  }
}

data class TwilightResult(val twilightBegin: LocalTime, val sunrise: LocalTime, val solarNoon: LocalTime, val sunset: LocalTime, val twilightEnd: LocalTime)

