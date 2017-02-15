package holmes.ponderosa.audio

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class AudioRequest {
  val audioManager: AudioManager

  init {
    val russoundCommands = RussoundCommands()

    // Not sure what we want to do w/ this for now.
    val outputStream: OutputStream
    if (File("/dev/ttyUSB0").exists()) {
      outputStream = FileOutputStream("/dev/ttyUSB0")
    } else if (File("/dev/ttyUSB0").exists()) {
      outputStream = FileOutputStream("/dev/tty.usbserial0")
    } else {
      outputStream = ByteArrayOutputStream()
    }

    audioManager = AudioManager(outputStream, russoundCommands)
  }

  fun status(zoneId: Int) {
    audioManager.getStatus(zone(zoneId))
  }

  fun power(zoneId: Int, turnOn: Boolean) {
    audioManager.power(zone(zoneId), turnOn)
  }

  fun change(sourceId: Int, zoneId: Int) {
    audioManager.changeSource(source(sourceId), zone(zoneId))
  }

  private fun zone(zoneId: Int): RussoundCommands.Zone {
    return when (zoneId) {
      0 -> RussoundCommands.Zone(0, "Family Room")
      1 -> RussoundCommands.Zone(1, "Kitchen")
      2 -> RussoundCommands.Zone(2, "Outside")
      3 -> RussoundCommands.Zone(3, "Master")
      4 -> RussoundCommands.Zone(4, "Nursery")
      else -> error("Unknown zone")
    }
  }

  private fun source(sourceId: Int): RussoundCommands.Source {
    return when (sourceId) {
      0 -> RussoundCommands.Source(0, "Family Room TV")
      1 -> RussoundCommands.Source(1, "Chromecast")
      else -> error("Unknown zone")
    }
  }
}
