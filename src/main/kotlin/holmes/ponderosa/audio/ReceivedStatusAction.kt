package holmes.ponderosa.audio

class ReceivedStatusAction(override val input: ByteArray) : RussoundAction {
  override val zoneOffset = 12

  override fun handle(currentZoneInfo: ZoneInfo): ZoneInfo {
    val zone = input[12].toInt()
    val source = input[21].toInt()

    // Convert from Russound code to something human readable.
    val power = input[20] == 1.toByte()
    val volume = input[22].toInt() * 2
    val bass = input[23].toInt() - 10
    val treble = input[24].toInt() - 10
    val loudness = input[25] == 1.toByte()
    val balance = input[26].toInt() - 10

    return ZoneInfo(zone, source, power, volume, bass, treble, balance, loudness)
  }
}

class ReceivedStatusActionHandler(val zones: Zones, val sources: Sources) : RussoundActionHandler {
  override fun createAction(input: ByteArray): RussoundAction {
    return ReceivedStatusAction(input)
  }

  override val infoOffsets: Set<Int>
      = setOf(12).plus((20..30).toSet())

  override val command: ByteArray = byteArrayOf(
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
