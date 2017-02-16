package holmes.ponderosa.audio

import java.io.OutputStream

class AudioManager(val russoundCommands: RussoundCommands, val outputStream: OutputStream) {
  fun getStatus(zone: Zone) {
    val command = russoundCommands.requestSource(zone)
    sendCommand(command)
  }

  fun power(zone: Zone, turnOn: Boolean) {
    val command: ByteArray
    if (turnOn) {
      command = russoundCommands.turnOn(zone)
    } else {
      command = russoundCommands.turnOff(zone)
    }

    sendCommand(command)
  }

  fun changeSource(source: Source, zone: Zone) {
    sendCommand(russoundCommands.turnOn(zone))
    Thread.sleep(500)
    sendCommand(russoundCommands.listen(source, zone))
  }

  private fun sendCommand(command: ByteArray) {
    outputStream.write(command)
    outputStream.flush()
  }
}
