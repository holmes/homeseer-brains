package holmes.ponderosa.audio

import holmes.ponderosa.util.toHexString

/**
 * The data I'm receiving doesn't seem to match the spec. So in case it changes we'll just
 * work from offsets as to where the data starts looking the same.
 */
data class ReceivedZoneInfo(val rawData: ByteArray) {
  val zoneId: Int
    get() = rawData[12].toInt()
  val power: Boolean
    get() = rawData[20] == 1.toByte()
  val sourceId: Int
    get() = rawData[21].toInt()
  val volume: Int
    get() = rawData[22].toInt() * 2
  val bass: Int
    get() = rawData[23].toInt()
  val treble: Int
    get() = rawData[24].toInt()
  val loudness: Boolean
    get() = rawData[25] == 1.toByte()
  val balance: Int
    get() = rawData[26].toInt()
  val systemOn: Boolean
    get() = rawData[27] == 1.toByte()

  override fun toString(): String {
    return rawData.toHexString()
  }

  companion object Factory {
    fun from(data: ByteArray): ReceivedZoneInfo? {
      // Length check is all we care about enough for now.
      return if (data.size == 34) ReceivedZoneInfo(data) else null
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
