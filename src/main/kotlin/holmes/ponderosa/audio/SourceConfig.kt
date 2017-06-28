package holmes.ponderosa.audio

import com.thejholmes.russound.Source

data class SourceConfig(val controllerId: Int, val sourceId: Int, val sourceNumber: Int, val name: String) {
  val source: Source
    get() = Source(controllerId, sourceNumber)
}

class Sources {
  val all: Map<Int, SourceConfig>
    get() = listOf(
        SourceConfig(0, 20, 0, "Family Room TV"),
        SourceConfig(0, 21, 1, "Chromecast"),
        SourceConfig(0, 22, 2, "Empty"),
        SourceConfig(0, 23, 3, "Empty"),
        SourceConfig(0, 24, 4, "Empty"),
        SourceConfig(0, 25, 5, "Empty")
    ).associateBy { it.sourceId }

  fun source(sourceId: Int): SourceConfig {
    return all.getOrElse(sourceId) {
      throw IllegalArgumentException("Unknown SourceConfig: $sourceId")
    }
  }

  fun sourceAt(sourceNumber: Int): SourceConfig {
    val source = all.values.firstOrNull { it.sourceNumber == sourceNumber }

    return when (source) {
      null -> throw IllegalArgumentException("Unknown SourceConfig Number: $sourceNumber")
      else -> source
    }
  }
}
