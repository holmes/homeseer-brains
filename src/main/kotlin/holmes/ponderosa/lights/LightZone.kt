package holmes.ponderosa.lights

import holmes.ponderosa.lights.LightZone.DependentZone.DependentTimeFrame
import java.time.LocalTime
import javax.inject.Provider

/**
 * A TimeFrame is a description of a light level. These are stacked togeher to
 * represent how we want to light up at a certain time of day.
 */
data class TimeFrame(val endTime: () -> LocalTime, val lowLevel: Int, val highLevel: Int) {
  fun contains(currentTime: LocalTime): Boolean {
    return endTime().isAfter(currentTime)
  }

  companion object Factory {
    fun StaticTimeFrame(time: LocalTime, lowLevel: Int, highLevel: Int): TimeFrame {
      return TimeFrame({ time }, lowLevel, highLevel)
    }
  }
}

data class  LightZone(val deviceId: Int, val subZones: Set<DependentZone>, val timeFrames: List<TimeFrame>) {
  data class DependentZone(val deviceId: Int, val timeFrames: List<DependentTimeFrame>) {
    data class DependentTimeFrame(val startTime: () -> LocalTime, val endTime: () -> LocalTime)

    fun contains(now: Provider<LocalTime>): Boolean {
      return timeFrames.any { it.startTime() < now.get() && it.endTime() > now.get() }
    }
  }

  fun isInFirstFrame(now: Provider<LocalTime>): Boolean {
    return timeFrames[0].contains(now.get())
  }

  fun previousFrame(now: Provider<LocalTime>): TimeFrame {
    return timeFrames.last { !it.contains(now.get()) }
  }

  fun currentFrame(now: Provider<LocalTime>): TimeFrame {
    return timeFrames.first { it.contains(now.get()) }
  }
}

class LightZones(twilight: Twilight) {
  private val zones = HashMap<Int, LightZone>()

  init {
    zones.put(
        // Family Room
        22, LightZone(22,
        setOf(
            LightZone.DependentZone(19, listOf(DependentTimeFrame(MIN, twilight.sunrise())))
        ),
        listOf(
            TimeFrame(twilight.sunrise(), 1, 35),
            TimeFrame(twilight.sunrise(60), 30, 60),
            TimeFrame(twilight.solarNoon(), 60, 85),
            TimeFrame(twilight.twilightEnd(), 30, 60),
            TimeFrame(MAX, 1, 35)
        )))
    zones.put(
        // Nursery
        28, LightZone(28, setOf(),
        listOf(
            TimeFrame({ LocalTime.of(8, 0) }, 1, 60),
            TimeFrame(twilight.sunrise(120), 30, 80),
            TimeFrame(twilight.solarNoon(), 80, 100),
            TimeFrame(twilight.twilightEnd(), 50, 80),
            TimeFrame({ LocalTime.of(22, 0) }, 30, 80),
            TimeFrame(MAX, 1, 60)
        ))
    )
    zones.put(
        // Guest Bath
        25, LightZone(25, setOf(),
        listOf(
            TimeFrame(twilight.twilightBegin(), 5, 35),
            TimeFrame(twilight.solarNoon(), 60, 85),
            TimeFrame(twilight.twilightEnd(), 40, 60),
            TimeFrame(MAX, 5, 50)
        ))
    )
    zones.put(
        // Kitchen
        19, LightZone(19, setOf(),
        listOf(
            TimeFrame(twilight.sunrise(), 10, 35),
            TimeFrame(twilight.sunrise(120), 30, 60),
            TimeFrame(twilight.solarNoon(), 60, 85),
            TimeFrame(twilight.twilightEnd(), 30, 60),
            TimeFrame(MAX, 18, 50)
        ))
    )
    zones.put(
        // Dining Room
        15, LightZone(15, setOf(),
        listOf(
            TimeFrame(twilight.sunrise(), 5, 35),
            TimeFrame(twilight.sunrise(120), 15, 35),
            TimeFrame(twilight.solarNoon(), 30, 85),
            TimeFrame(twilight.twilightEnd(), 15, 60),
            TimeFrame(MAX, 5, 40)
        ))
    )
  }

  fun zone(deviceId: Int): LightZone? {
    return zones[deviceId]
  }

  private val MIN: () -> LocalTime
    get() = { LocalTime.MIN }

  private val MAX: () -> LocalTime
    get() = { LocalTime.MAX }
}
