package holmes.ponderosa.audio

import holmes.ponderosa.util.toHexString

/**
 * The data I'm receiving doesn't seem to match the spec. So in case it changes we'll just
 * work from offsets as to where the data starts looking the same.
 */
data class ReceivedZoneInfo(val rawData: ByteArray, val startOffset: Int) {
  val zoneId: Int
    get() = rawData[startOffset + 2].toInt()
  val power: Boolean
    get() = rawData[startOffset + 10] == 1.toByte()
  val sourceId: Int
    get() = rawData[startOffset + 11].toInt()
  val volume: Int
    get() = rawData[startOffset + 12].toInt() * 2
  val bass: Int
    get() = rawData[startOffset + 13].toInt()
  val treble: Int
    get() = rawData[startOffset + 14].toInt()
  val loudness: Boolean
    get() = rawData[startOffset + 15] == 1.toByte()
  val balance: Int
    get() = rawData[startOffset + 16].toInt()
  val systemOn: Boolean
    get() = rawData[startOffset + 17] == 1.toByte()

  override fun toString(): String {
    return rawData.toHexString()
  }

  companion object Factory {
    fun from(data: ByteArray): ReceivedZoneInfo? {
      // Length check is all we care about enough for now.
      if (data.size != 31) return null

      val startOffset = data.indexOf(2)
      return ReceivedZoneInfo(data, startOffset)
    }
  }

  override fun equals(other: Any?): Boolean {
    return when (other) {
      !is ReceivedZoneInfo -> false
      else -> rawData.contentEquals(other.rawData)
    }
  }

  override fun hashCode(): Int {
    return rawData.contentHashCode()
  }
}
