package holmes.ponderosa.audio

/**
 * @param zoneId unique id of this zone
 * @param zoneNumber represents the port on the back, 1-based
 */
data class Zone(val controllerId: Int, val zoneId: Int, val zoneNumber: Int, val name: String) {
  init {
    if(zoneNumber <= 0) throw IllegalArgumentException("Invalid Zone ($zoneId). Zones are 1-based")
  }
}

class Zones {
  val all: Map<Int, Zone>
    get() = mapOf(
        Pair(0, Zone(0, 0, 1, "Family Room")),
        Pair(1, Zone(0, 1, 2, "Kitchen")),
        Pair(2, Zone(0, 2, 3, "Outside")),
        Pair(3, Zone(0, 3, 4, "Master")),
        Pair(4, Zone(0, 4, 5, "Nursery")),
        Pair(5, Zone(0, 5, 6, "Unknown")))

  fun zone(zoneId: Int): Zone {
    val zone = all[zoneId]

    return when (zone) {
      null -> throw IllegalArgumentException("Unknown Zone: $zoneId")
      else -> zone
    }
  }
}
