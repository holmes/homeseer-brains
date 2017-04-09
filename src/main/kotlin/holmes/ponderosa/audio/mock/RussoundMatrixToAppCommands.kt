package holmes.ponderosa.audio.mock

import holmes.ponderosa.audio.RussoundCommands.Bytes.calculateChecksum
import holmes.ponderosa.audio.ZoneInfo

class RussoundMatrixToAppCommands {
  companion object Responses {
    fun returnSource(zoneInfo: ZoneInfo): ByteArray {
      val bytes = sourceBytes

      bytes[12] = zoneInfo.zone.toByte()
      bytes[20] = zoneInfo.source.toByte()
      bytes[21] = calculateChecksum(bytes)

      return bytes
    }

    fun returnPower(zoneInfo: ZoneInfo): ByteArray {
      val bytes = powerBytes

      bytes[12] = zoneInfo.zone.toByte()
      bytes[20] = if (zoneInfo.power) 1 else 0
      bytes[21] = calculateChecksum(bytes)

      return bytes
    }

    fun returnVolume(zoneInfo: ZoneInfo): ByteArray {
      val bytes = volumeBytes

      bytes[12] = zoneInfo.zone.toByte()
      bytes[20] = (zoneInfo.volume / 2).toByte()
      bytes[21] = calculateChecksum(bytes)

      return bytes
    }

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

    private val sourceBytes: ByteArray
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
          0x02.toByte(),
          0x00.toByte(),
          0x00.toByte(),
          0x01.toByte(),
          0x00.toByte(),
          0x01.toByte(),
          0x00.toByte(),
          0x00.toByte(),
          0x00.toByte(),
          0xf7.toByte()
      )

    private val powerBytes: ByteArray
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
          0x06.toByte(),
          0x00.toByte(),
          0x00.toByte(),
          0x01.toByte(),
          0x00.toByte(),
          0x01.toByte(),
          0x00.toByte(),
          0x00.toByte(),
          0x00.toByte(),
          0xf7.toByte()
      )

    private val volumeBytes: ByteArray
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
          0x01.toByte(),
          0x00.toByte(),
          0x00.toByte(),
          0x01.toByte(),
          0x00.toByte(),
          0x01.toByte(),
          0x00.toByte(),
          0x00.toByte(),
          0x00.toByte(),
          0xf7.toByte()
      )

    private val statusBytes: ByteArray
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
