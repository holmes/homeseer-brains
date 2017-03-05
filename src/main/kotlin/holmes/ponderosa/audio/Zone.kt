package holmes.ponderosa.audio

/**
 * @param zoneId unique id of this zone
 * @param zoneNumber represents the port on the back, 1-based
 */
data class Zone(val controllerId: Int, val zoneId: Int, val zoneNumber: Int, val name: String)

class Zones {
  val all: Map<Int, Zone>
    get() = listOf(
        Zone(0, 10, 0, "Family Room"),
        Zone(0, 11, 1, "Kitchen"),
        Zone(0, 12, 2, "Outside"),
        Zone(0, 13, 3, "Master"),
        Zone(0, 14, 4, "Nursery"),
        Zone(0, 15, 5, "Unknown")
    ).associateBy(Zone::zoneId)

  fun zone(zoneId: Int): Zone {
    return all.getOrElse(zoneId) {
      throw IllegalArgumentException("Unknown Zone: $zoneId")
    }
  }

  fun zoneAt(zoneNumber: Int): Zone {
    val zone = all.values.firstOrNull { it.zoneNumber == zoneNumber }

    return when (zone) {
      null -> throw IllegalArgumentException("Unknown Zone Number: $zoneNumber")
      else -> zone
    }
  }
}
