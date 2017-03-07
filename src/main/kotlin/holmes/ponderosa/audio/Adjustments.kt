package holmes.ponderosa.audio

enum class PowerChange {
  ON, OFF;
}

sealed class VolumeChange {
  class Up : VolumeChange()
  class Down : VolumeChange()
  class Set(val level: Int) : VolumeChange()
}

enum class BassLevel {
  UP, DOWN, FLAT;
}

enum class TrebleLevel {
  UP, DOWN, FLAT;
}

enum class Balance {
  LEFT, RIGHT, CENTER;
}

enum class Loudness {
  ON, OFF;
}
