package holmes.ponderosa.audio.mock

import holmes.ponderosa.audio.RussoundCommands.Bytes.calculateChecksum
import holmes.ponderosa.audio.ZoneInfo

class RussoundMatrixToAppCommands {
  fun returnStatus(zoneInfo: ZoneInfo): ByteArray {
    val bytes = statusBytes

    bytes[12] = zoneInfo.zone.toByte()
    bytes[21] = zoneInfo.source.toByte()

    // Convert from normal values to Russound code.
    bytes[20] = if (zoneInfo.power) 1 else 0
    bytes[22] = (zoneInfo.volume / 2).toByte()
    bytes[23] = (zoneInfo.bass - 10).toByte()
    bytes[24] = (zoneInfo.treble - 10).toByte()
    bytes[25] = if (zoneInfo.loudness) 1 else 0
    bytes[26] = (zoneInfo.balance - 10).toByte()
    bytes[32] = calculateChecksum(bytes)

    return bytes
  }

  companion object Bytes {
    val statusBytes: ByteArray
      get() = byteArrayOf(
          0xf0.toByte(),
          0x00.toByte(),
          0x00.toByte(),
          0x70.toByte(),
          0x00.toByte(),
          0x00.toByte(),
          0x7f.toByte(),
          0x00.toByte(),
          0x00.toByte(),
          0x04.toByte(),
          0x02.toByte(),
          0x00.toByte(),
          0x00.toByte(),
          0x07.toByte(),
          0x00.toByte(),
          0x00.toByte(),
          0x01.toByte(),
          0x00.toByte(),
          0x0c.toByte(),
          0x00.toByte(),
          0x00.toByte(),
          0x00.toByte(),
          0x00.toByte(),
          0x00.toByte(),
          0x00.toByte(),
          0x00.toByte(),
          0x00.toByte(),
          0x00.toByte(),
          0x00.toByte(),
          0x00.toByte(),
          0x00.toByte(),
          0x00.toByte(),
          0x00.toByte(),
          0xf7.toByte()
      )
  }
}
