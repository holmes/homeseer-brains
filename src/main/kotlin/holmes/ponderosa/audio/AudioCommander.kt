package holmes.ponderosa.audio

import java.io.OutputStream

class AudioCommander(val russoundCommands: RussoundCommands, val outputStream: OutputStream) {

  fun requestStatus(zone: Zone) {
    val command = russoundCommands.requestStatus(zone)
    sendCommand(command)
  }

  fun power(zone: Zone, power: PowerChange) {
    sendCommand(when (power) {
      PowerChange.ON -> russoundCommands.turnOn(zone)
      PowerChange.OFF -> russoundCommands.turnOff(zone)
    })
  }

  fun volume(zone: Zone, volume: VolumeChange) {
    sendCommand(when (volume) {
      is VolumeChange.Up -> russoundCommands.volumeUp(zone)
      is VolumeChange.Down -> russoundCommands.volumeDown(zone)
      is VolumeChange.Set -> russoundCommands.volume(zone, volume.level)
    })
  }

  fun changeSource(zone: Zone, source: Source) {
    sendCommand(russoundCommands.listen(zone, source))
  }

  private fun sendCommand(command: ByteArray) {
    outputStream.write(command)
    outputStream.flush()
  }
}
