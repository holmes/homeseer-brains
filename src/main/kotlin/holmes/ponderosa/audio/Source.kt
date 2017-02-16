package holmes.ponderosa.audio

class Sources {
  val all: Array<Source>
    get() = arrayOf(
        Source(0, "Family Room TV"),
        Source(1, "Chromecast")
    )

  fun source(sourceId: Int): Source? = when (sourceId) {
    in 0..all.size -> all[sourceId]
    else -> null
  }
}

data class Source(val sourceId: Int, val name: String) {
  /** The Display number, 1-based. */
  val sourceNumber: String
    get() = (sourceId + 1).toString()
}
