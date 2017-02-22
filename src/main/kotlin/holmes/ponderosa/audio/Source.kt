package holmes.ponderosa.audio

data class Source(val controllerId: Int, val sourceId: Int, val sourceNumber: Int, val name: String) {
  init {
    if (sourceNumber <= 0) throw IllegalArgumentException("Invalid Source ($sourceId). Sources are 1-based")
  }
}

class Sources {
  val all: Set<Source>
    get() = setOf(
        Source(0, 0, 1, "Family Room TV"),
        Source(0, 1, 2, "Chromecast")
    )

  fun source(sourceId: Int): Source = when (sourceId) {
    in 0..all.size -> all.first { it.sourceId == sourceId }
    else -> throw IllegalArgumentException("Unknown Source: $sourceId")
  }
}
