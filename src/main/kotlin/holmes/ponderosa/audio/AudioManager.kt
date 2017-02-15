package holmes.ponderosa.audio

import java.io.OutputStream

class AudioManager(val outputStream: OutputStream, val russoundCommands: RussoundCommands) {
  fun power(zone: RussoundCommands.Zone, turnOn: Boolean) {
    val command: ByteArray
    if (turnOn) {
      command = russoundCommands.turnOn(zone)
    } else {
      command = russoundCommands.turnOff(zone)
    }

    sendCommand(command)
  }

  fun changeSource(source: RussoundCommands.Source, zone: RussoundCommands.Zone) {
    sendCommand(russoundCommands.turnOn(zone))
    sendCommand(russoundCommands.listen(source, zone))
  }

  private fun sendCommand(command: ByteArray) {
    outputStream.write(command)
    outputStream.flush()
  }
}
