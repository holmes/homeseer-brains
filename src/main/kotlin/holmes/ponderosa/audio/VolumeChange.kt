package holmes.ponderosa.audio

sealed class VolumeChange {
  class Up : VolumeChange()
  class Down : VolumeChange()
  class Set(val level: Int) : VolumeChange()
}
