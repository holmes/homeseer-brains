package holmes.ponderosa.audio

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class RussoundUnitTest {
  lateinit var russound: Russound
  lateinit var zone0: Russound.Zone
  lateinit var zone1: Russound.Zone
  lateinit var source0: Russound.Source
  lateinit var source1: Russound.Source

  @Before fun setUp() {
    zone0 = Russound.Zone(0, "Kitchen")
    zone1 = Russound.Zone(1, "Outside")
    source0 = Russound.Source(0, "TV Family Room")
    source1 = Russound.Source(1, "Chromecast")
    russound = Russound()
  }

  @Test fun testCheckSum() {
    val bytes = "F000677CF10F00F7".toByteArray()
    assertThat(russound.calculateChecksum(bytes)).isEqualTo(0x59.toByte())
  }

  @Test fun testZone0PowerOn() {
    val powerOn = "F000007F0000700502020000F12300010000000112F7".toByteArray()
    assertThat(russound.turnOn(zone0)).isEqualTo(powerOn)
  }

  @Test fun testZone0PowerOff() {
    val powerOff = "F000007F0000700502020000F12300000000000111F7".toByteArray()
    assertThat(russound.turnOff(zone0)).isEqualTo(powerOff)
  }

  @Test fun testZone1PowerOn() {
    val powerOn = "F000007F0000700502020000F12300010001000113F7".toByteArray()
    assertThat(russound.turnOn(zone1)).isEqualTo(powerOn)
  }

  @Test fun testZone1PowerOff() {
    val powerOff = "F000007F0000700502020000F12300000001000112F7".toByteArray()
    assertThat(russound.turnOff(zone1)).isEqualTo(powerOff)
  }

  @Test fun testSource0Zone0() {
    val expected = "F000007F0000700502000000F13E0000000000012AF7".toByteArray()
    assertThat(russound.listen(toSource = source0, inZone = zone0)).isEqualTo(expected)
  }

  @Test fun testSource1Zone0() {
    val expected = "F000007F0000700502000000F13E0000000100012BF7".toByteArray()
    assertThat(russound.listen(toSource = source1, inZone = zone0)).isEqualTo(expected)
  }

  @Test fun testSource0Zone1() {
    val expected = "F000007F0001700502000000F13E0000000000012BF7".toByteArray()
    assertThat(russound.listen(toSource = source0, inZone = zone1)).isEqualTo(expected)
  }

  @Test fun testSource1Zone1() {
    val expected = "F000007F0001700502000000F13E0000000100012CF7".toByteArray()
    assertThat(russound.listen(toSource = source1, inZone = zone1)).isEqualTo(expected)
  }
}


private val HEX_CHARS = "0123456789ABCDEF"

fun String.toByteArray() : ByteArray {
  val result = ByteArray(length / 2)

  for (i in 0 until length step 2) {
    val firstIndex = HEX_CHARS.indexOf(this[i]);
    val secondIndex = HEX_CHARS.indexOf(this[i + 1]);

    val octet = firstIndex.shl(4).or(secondIndex)
    result[i.shr(1)] = octet.toByte()
  }

  return result
}
