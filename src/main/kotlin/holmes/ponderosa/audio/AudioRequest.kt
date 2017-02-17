package holmes.ponderosa.audio

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class AudioRequest {
  val bytesWritten: ByteArrayOutputStream = ByteArrayOutputStream()
  val audioCommander: AudioCommander

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
    audioCommander = AudioCommander(russoundCommands, actualOutputStream)
  }

  fun status(zone: Zone) {
    audioCommander.requestStatus(zone)
  }

  fun power(zone: Zone, turnOn: Boolean): ByteArray {
    audioCommander.power(zone, turnOn)
    return bytesWritten.toByteArray()
  }

  fun change(source: Source, zone: Zone) {
    audioCommander.changeSource(source, zone)
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
