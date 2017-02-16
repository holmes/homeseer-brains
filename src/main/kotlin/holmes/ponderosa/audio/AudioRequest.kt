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

  fun status(zone: RussoundCommands.Zone) {
    audioManager.getStatus(zone)
  }

  fun power(zone: RussoundCommands.Zone, turnOn: Boolean): ByteArray {
    audioManager.power(zone, turnOn)
    return bytesWritten.toByteArray()
  }

  fun change(source: RussoundCommands.Source, zone: RussoundCommands.Zone) {
    audioManager.changeSource(source, zone)
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
