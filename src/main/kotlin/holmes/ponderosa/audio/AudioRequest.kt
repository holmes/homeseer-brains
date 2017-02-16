package holmes.ponderosa.audio

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class AudioRequest {
  val bytesWritten: ByteArrayOutputStream = ByteArrayOutputStream()
  val audioManager: AudioManager

  init {
    // Not sure what we want to do w/ this for now.
    val outputStream: OutputStream
    if (File("/dev/ttyUSB0").exists()) {
      outputStream = FileOutputStream("/dev/ttyUSB0")
    } else if (File("/dev/ttyUSB0").exists()) {
      outputStream = FileOutputStream("/dev/tty.usbserial0")
    } else {
      outputStream = ByteArrayOutputStream()
    }
    val actualOutputStream = TeeOutputStream(bytesWritten, outputStream)

    val russoundCommands = RussoundCommands()
    audioManager = AudioManager(russoundCommands, actualOutputStream)
  }

  fun status(zoneId: Int) {
    audioManager.getStatus(zone(zoneId))
  }

  fun power(zoneId: Int, turnOn: Boolean): ByteArray {
    audioManager.power(zone(zoneId), turnOn)
    return bytesWritten.toByteArray()
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

private class TeeOutputStream(vararg val outputStreams:OutputStream): OutputStream() {
  override fun write(b: Int) {
    outputStreams.forEach { it.write(b) }
  }

  override fun write(b: ByteArray?) {
    outputStreams.forEach { it.write(b) }
  }

  override fun write(b: ByteArray?, off: Int, len: Int) {
    outputStreams.forEach { it.write(b, off, len) }
  }

  override fun flush() {
    outputStreams.forEach { it.flush() }
  }

  override fun close() {
    outputStreams.forEach { it.close() }
  }
}
