package holmes.ponderosa.audio

class RussoundCommands {
  data class Source(val sourceId: Int, val name: String) {
    /** The Display number, 1-based. */
    val sourceNumber: String
      get() = (sourceId + 1).toString()
  }

  data class Zone(val zoneId: Int, val name: String) {
    /** The Display number, 1-based. */
    val zoneNumber: String
      get() = (zoneId + 1).toString()
  }

  fun turnOn(zone: Zone): ByteArray {
    val bytes = powerBytes

    bytes[15] = 0x01.toByte()
    bytes[17] = zone.zoneId.toByte()
    bytes[20] = calculateChecksum(bytes)

    return bytes
  }

  fun turnOff(zone: Zone): ByteArray {
    val bytes = powerBytes

    bytes[15] = 0x00.toByte()
    bytes[17] = zone.zoneId.toByte()
    bytes[20] = calculateChecksum(bytes)

    return bytes
  }

  fun listen(toSource: Source, inZone: Zone): ByteArray {
    val bytes = sourceSelectBytes

    bytes[5] = inZone.zoneId.toByte()
    bytes[17] = toSource.sourceId.toByte()
    bytes[20] = calculateChecksum(bytes)

    return bytes
  }

  fun volumeUp(inZone: Zone): ByteArray {
    return byteArrayOf(10, 20, 30)
  }

  fun volumeDown(inZone: Zone): ByteArray {
    return byteArrayOf(10, 20, 30)
  }

  internal fun calculateChecksum(bytes: ByteArray): Byte {
    val step1 = bytes.dropLast(2).sum()
    val step2 = step1 + bytes.size - 2
    val step3 = 0x007f.and(step2)
    return step3.toByte()
  }

  private val powerBytes: ByteArray
    get() = byteArrayOf(
        0xf0.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x7f.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x70.toByte(),
        0x05.toByte(),
        0x02.toByte(),
        0x02.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0xf1.toByte(),
        0x23.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x01.toByte(),
        0x00.toByte(),
        0xf7.toByte()
    )
  private val sourceSelectBytes: ByteArray
    get() = byteArrayOf(
        0xf0.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x7f.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x70.toByte(),
        0x05.toByte(),
        0x02.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0xf1.toByte(),
        0x3e.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x01.toByte(),
        0x00.toByte(),
        0xf7.toByte()
    )
}
