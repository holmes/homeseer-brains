package holmes.ponderosa.audio

enum class PowerChange {
  ON, OFF;

  val isOn: Boolean
    get() = this == ON
}
