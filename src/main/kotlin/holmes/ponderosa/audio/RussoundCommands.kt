package holmes.ponderosa.audio

class RussoundCommands {
  fun requestStatus(zone: Zone): ByteArray {
    val bytes = statusBytes

    bytes[11] = zone.zoneNumber.minus(1).toByte()
    bytes[15] = calculateChecksum(bytes)

    return bytes
  }

  fun turnOn(zone: Zone): ByteArray {
    val bytes = powerBytes

    bytes[15] = 0x01.toByte()
    bytes[17] = zone.zoneNumber.minus(1).toByte()
    bytes[20] = calculateChecksum(bytes)

    return bytes
  }

  fun turnOff(zone: Zone): ByteArray {
    val bytes = powerBytes

    bytes[15] = 0x00.toByte()
    bytes[17] = zone.zoneNumber.minus(1).toByte()
    bytes[20] = calculateChecksum(bytes)

    return bytes
  }

  fun listen(zone: Zone, source: Source): ByteArray {
    val bytes = sourceSelectBytes

    bytes[5] = zone.zoneNumber.minus(1).toByte()
    bytes[17] = source.sourceId.toByte()
    bytes[20] = calculateChecksum(bytes)

    return bytes
  }

  fun bassUp(zone: Zone): ByteArray {
    val bytes = bassAdjustBytes

    bytes[11] = zone.zoneNumber.minus(1).toByte()
    bytes[15] = 0x69
    bytes[22] = calculateChecksum(bytes)

    return bytes
  }

  fun bassDown(zone: Zone): ByteArray {
    val bytes = bassAdjustBytes

    bytes[11] = zone.zoneNumber.minus(1).toByte()
    bytes[15] = 0x6A
    bytes[22] = calculateChecksum(bytes)

    return bytes
  }

  fun bassFlat(zone: Zone): ByteArray {
    val bytes = bassSetBytes

    bytes[11] = zone.zoneNumber.minus(1).toByte()
    bytes[21] = 0x0A
    bytes[22] = calculateChecksum(bytes)

    return bytes
  }

  fun trebleUp(zone: Zone): ByteArray {
    val bytes = trebleBytes

    bytes[11] = zone.zoneNumber.minus(1).toByte()
    bytes[15] = 0x69
    bytes[22] = calculateChecksum(bytes)

    return bytes
  }

  fun trebleDown(zone: Zone): ByteArray {
    val bytes = trebleBytes

    bytes[11] = zone.zoneNumber.minus(1).toByte()
    bytes[15] = 0x6A
    bytes[22] = calculateChecksum(bytes)

    return bytes
  }

  fun trebleFlat(zone: Zone): ByteArray {
    val bytes = trebleSetBytes

    bytes[11] = zone.zoneNumber.minus(1).toByte()
    bytes[21] = 0x0A
    bytes[22] = calculateChecksum(bytes)

    return bytes
  }

  fun loudness(zone: Zone): ByteArray {
    val bytes = loudnessBytes

    bytes[11] = zone.zoneNumber.minus(1).toByte()
    bytes[15] = 0x69
    bytes[22] = calculateChecksum(bytes)

    return bytes
  }

  fun balanceLeft(zone: Zone): ByteArray {
    val bytes = balanceAdjustBytes

    bytes[11] = zone.zoneNumber.minus(1).toByte()
    bytes[15] = 0x69
    bytes[22] = calculateChecksum(bytes)

    return bytes
  }

  fun balanceRight(zone: Zone): ByteArray {
    val bytes = balanceAdjustBytes

    bytes[11] = zone.zoneNumber.minus(1).toByte()
    bytes[15] = 0x6A
    bytes[22] = calculateChecksum(bytes)

    return bytes
  }

  fun balanceCentered(zone: Zone): ByteArray {
    val bytes = balanceSetBytes

    bytes[11] = zone.zoneNumber.minus(1).toByte()
    bytes[21] = 0x0A
    bytes[22] = calculateChecksum(bytes)

    return bytes
  }

  fun volume(zone: Zone, level: Int): ByteArray {
    val bytes = setVolumeBytes

    bytes[15] = (level / 2).toByte()
    bytes[17] = zone.zoneNumber.minus(1).toByte()
    bytes[20] = calculateChecksum(bytes)

    return bytes
  }

  fun volumeUp(zone: Zone): ByteArray {
    val bytes = volumeUpBytes

    bytes[5] = zone.zoneNumber.minus(1).toByte()
    bytes[19] = calculateChecksum(bytes)

    return bytes
  }

  fun volumeDown(zone: Zone): ByteArray {
    val bytes = volumeDownBytes

    bytes[5] = zone.zoneNumber.minus(1).toByte()
    bytes[20] = calculateChecksum(bytes)

    return bytes
  }

  fun turnOnVolume(zone: Zone, level: Int): ByteArray {
    val bytes = setTurnOnVolumeBytes

    bytes[11] = zone.zoneNumber.minus(1).toByte()
    bytes[21] = (level / 2).toByte()
    bytes[22] = calculateChecksum(bytes)

    return bytes
  }

  private fun calculateChecksum(bytes: ByteArray): Byte {
    val step1 = bytes.dropLast(2).sum()
    val step2 = step1 + bytes.size - 2
    val step3 = 0x007f.and(step2)
    return step3.toByte()
  }

  private val statusBytes: ByteArray
    get() = byteArrayOf(
        0xf0.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x7f.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x70.toByte(),
        0x01.toByte(),
        0x04.toByte(),
        0x02.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x07.toByte(),
        0x00.toByte(),
        0x00.toByte(),
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

  private val setTurnOnVolumeBytes: ByteArray
    get() = byteArrayOf(
        0xf0.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x7f.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x70.toByte(),
        0x00.toByte(),
        0x05.toByte(),
        0x02.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x04.toByte(),
        0x00.toByte(),
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

  private val bassAdjustBytes: ByteArray
    get() = byteArrayOf(
        0xf0.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x7f.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x70.toByte(),
        0x05.toByte(),
        0x05.toByte(),
        0x02.toByte(),
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
        0x01.toByte(),
        0x00.toByte(),
        0xf7.toByte()
    )

  private val bassSetBytes: ByteArray
    get() = byteArrayOf(
        0xf0.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x7f.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x70.toByte(),
        0x00.toByte(),
        0x05.toByte(),
        0x02.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
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

  private val trebleBytes: ByteArray
    get() = byteArrayOf(
        0xf0.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x7f.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x70.toByte(),
        0x05.toByte(),
        0x05.toByte(),
        0x02.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x01.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x01.toByte(),
        0x00.toByte(),
        0xf7.toByte()
    )

  private val trebleSetBytes: ByteArray
    get() = byteArrayOf(
        0xf0.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x7f.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x70.toByte(),
        0x00.toByte(),
        0x05.toByte(),
        0x02.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x01.toByte(),
        0x00.toByte(),
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

  private val loudnessBytes: ByteArray
    get() = byteArrayOf(
        0xf0.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x7f.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x70.toByte(),
        0x05.toByte(),
        0x05.toByte(),
        0x02.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x02.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x01.toByte(),
        0x00.toByte(),
        0xf7.toByte()
    )

  private val balanceAdjustBytes: ByteArray
    get() = byteArrayOf(
        0xf0.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x7f.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x70.toByte(),
        0x05.toByte(),
        0x05.toByte(),
        0x02.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x03.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x01.toByte(),
        0x00.toByte(),
        0xf7.toByte()
    )

  private val balanceSetBytes: ByteArray
    get() = byteArrayOf(
        0xf0.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x7f.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x70.toByte(),
        0x00.toByte(),
        0x05.toByte(),
        0x02.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x03.toByte(),
        0x00.toByte(),
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

  private val setVolumeBytes: ByteArray
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
        0x21.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x01.toByte(),
        0x00.toByte(),
        0xf7.toByte()
    )

  private val volumeUpBytes: ByteArray
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
        0x7f.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x01.toByte(),
        0x00.toByte(),
        0xf7.toByte()
    )

  private val volumeDownBytes: ByteArray
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
        0x7f.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x01.toByte(),
        0x00.toByte(),
        0xf7.toByte()
    )

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
}
