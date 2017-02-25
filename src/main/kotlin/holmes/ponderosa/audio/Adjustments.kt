package holmes.ponderosa.audio

enum class PowerChange {
  ON, OFF;

  val isOn: Boolean
    get() = this == ON
}

sealed class VolumeChange {
  class Up : VolumeChange()
  class Down : VolumeChange()
  class Set(val level: Int) : VolumeChange()

  val verb: String
    get() {
      return when (this) {
        is Up -> "up"
        is Down -> "down"
        is Set -> "to ${this.level}%"
      }
    }
}

enum class BassLevel {
  UP, DOWN, FLAT;

  val adjustment: Int get() = if (this == UP) 1 else -1
}

enum class TrebleLevel {
  UP, DOWN, FLAT;

  val adjustment: Int get() = if (this == UP) 1 else -1
}

enum class Balance {
  LEFT, RIGHT, CENTER;

  val adjustment: Int get() = if (this == RIGHT) 1 else -1
}

enum class Loudness {
  ON, OFF;

  val isOn: Boolean
    get() = this == ON
}
