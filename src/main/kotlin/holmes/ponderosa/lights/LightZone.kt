package holmes.ponderosa.lights

import java.time.LocalTime
import javax.inject.Provider

data class LightZone(val deviceId: Int, val subZones: Set<Int>, val timeFrames: List<TimeFrame>) {
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
        22, LightZone(22, setOf(), listOf(
        TimeFrame(twilight.twilightBegin(), 1, 35),
        TimeFrame(twilight.sunrise(), 1, 35),
        TimeFrame(twilight.solarNoon(), 30, 85),
        TimeFrame(twilight.twilightEnd(), 10, 60),
        TimeFrame(LocalTime.MAX, 1, 35)
    ))
    )
    zones.put(
        // Nursery
        28, LightZone(28, setOf(), listOf(
        TimeFrame(twilight.twilightBegin(), 1, 35),
        TimeFrame(twilight.sunrise(), 1, 35),
        TimeFrame(twilight.solarNoon(), 30, 85),
        TimeFrame(LocalTime.MAX, 1, 35)
    ))
    )
    zones.put(
        // Guest Bath
        25, LightZone(25, setOf(), listOf(
        TimeFrame(twilight.twilightBegin(), 5, 35),
        TimeFrame(twilight.solarNoon(), 35, 85),
        TimeFrame(twilight.twilightEnd(), 20, 60),
        TimeFrame(LocalTime.of(23, 59), 5, 35)
    ))
    )
    zones.put(
        // Kitchen
        19, LightZone(19, setOf(), listOf(
        TimeFrame(twilight.twilightBegin(), 10, 35),
        TimeFrame(twilight.solarNoon(), 35, 85),
        TimeFrame(twilight.twilightEnd(), 25, 60),
        TimeFrame(LocalTime.of(23, 59), 18, 35)
    ))
    )
    zones.put(
        // Dining Room
        15, LightZone(15, setOf(), listOf(
        TimeFrame(twilight.twilightBegin(), 5, 35),
        TimeFrame(twilight.solarNoon(), 30, 85),
        TimeFrame(twilight.twilightEnd(), 15, 60),
        TimeFrame(LocalTime.of(23, 59), 5, 35)
    ))
    )
  }

  fun zone(deviceId: Int): LightZone? {
    return zones[deviceId]
  }
}
