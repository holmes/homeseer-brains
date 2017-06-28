package holmes.ponderosa.audio

import org.holmes.russound.Zone

/**
 * @param zoneId unique id of this zone
 * @param zoneNumber represents the port on the back, 1-based
 */
data class ZoneConfig(val controllerId: Int, val zoneId: Int, val zoneNumber: Int, val name: String) {
  val zone : Zone
      get()= Zone(controllerId, zoneNumber)
}

class Zones {
  val all: Map<Int, ZoneConfig>
    get() = listOf(
        ZoneConfig(0, 10, 0, "Family Room"),
        ZoneConfig(0, 11, 1, "Kitchen"),
        ZoneConfig(0, 12, 2, "Outside"),
        ZoneConfig(0, 13, 3, "Master"),
        ZoneConfig(0, 14, 4, "Nursery"),
        ZoneConfig(0, 15, 5, "Unknown")
    ).associateBy(ZoneConfig::zoneId)

  fun zone(zoneId: Int): ZoneConfig {
    return all.getOrElse(zoneId) {
      throw IllegalArgumentException("Unknown ZoneConfig: $zoneId")
    }
  }

  fun zoneAt(zoneNumber: Int): ZoneConfig {
    val zone = all.values.firstOrNull { it.zoneNumber == zoneNumber }

    return when (zone) {
      null -> throw IllegalArgumentException("Unknown ZoneConfig Number: $zoneNumber")
      else -> zone
    }
  }
}
