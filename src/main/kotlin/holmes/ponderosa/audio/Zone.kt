package holmes.ponderosa.audio

class Zones {
  val all: Array<Zone>
    get() = arrayOf(
        Zone(0, "Family Room"),
        Zone(1, "Kitchen"),
        Zone(2, "Outside"),
        Zone(3, "Master"),
        Zone(4, "Nursery")
    )

  fun zone(zoneId: Int): Zone {
    when (zoneId) {
      in 0..all.size -> return all[zoneId]
      else -> throw IllegalArgumentException("Unknown Zone: $zoneId")
    }
  }
}

data class Zone(val zoneId: Int, val name: String) {
  /** The Display number, 1-based. */
  val zoneNumber: String
    get() = (zoneId + 1).toString()
}
