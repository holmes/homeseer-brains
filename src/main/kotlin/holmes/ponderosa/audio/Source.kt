package holmes.ponderosa.audio

data class Source(val controllerId: Int, val sourceId: Int, val sourceNumber: Int, val name: String)

class Sources {
  val all: Map<Int, Source>
    get() = listOf(
        Source(0, 20, 0, "Family Room TV"),
        Source(0, 21, 1, "Chromecast"),
        Source(0, 22, 2, "Empty"),
        Source(0, 23, 3, "Empty"),
        Source(0, 24, 4, "Empty"),
        Source(0, 25, 5, "Empty")
    ).associateBy { it.sourceId }

  fun source(sourceId: Int): Source {
    return all.getOrElse(sourceId) {
      throw IllegalArgumentException("Unknown Source: $sourceId")
    }
  }

  fun sourceAt(sourceNumber: Int): Source {
    val source = all.values.firstOrNull { it.sourceNumber == sourceNumber }

    return when (source) {
      null -> throw IllegalArgumentException("Unknown Source Number: $sourceNumber")
      else -> source
    }
  }
}
