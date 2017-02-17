package holmes.ponderosa.audio

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.io.OutputStream

class AudioCommander(val russoundCommands: RussoundCommands, val outputStream: OutputStream) {
  private val updatedZoneSubject = PublishSubject.create<ZoneInfo>()
  val updatedZone: Observable<ZoneInfo> = updatedZoneSubject

  fun requestStatus(zone: Zone) {
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

  fun volume(zone: Zone, level: Int) {
    sendCommand(russoundCommands.volume(zone, level))
  }

  fun volumeUp(zone: Zone) {
    sendCommand(russoundCommands.volumeUp(zone))
  }

  fun volumeDown(zone: Zone) {
    sendCommand(russoundCommands.volumeDown(zone))
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

  private fun receiveCommand() {
    val zoneInfo = ZoneInfo(Zone(1, ""), Source(1, ""), power = true, volume = 88)
  }
}
