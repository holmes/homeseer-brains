package holmes.ponderosa.audio

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class RussoundCommandsUnitTest {
  lateinit var russoundCommands: RussoundCommands
  lateinit var zone0: RussoundCommands.Zone
  lateinit var zone1: RussoundCommands.Zone
  lateinit var source0: RussoundCommands.Source
  lateinit var source1: RussoundCommands.Source

  @Before fun setUp() {
    zone0 = RussoundCommands.Zone(0, "Kitchen")
    zone1 = RussoundCommands.Zone(1, "Outside")
    source0 = RussoundCommands.Source(0, "TV Family Room")
    source1 = RussoundCommands.Source(1, "Chromecast")
    russoundCommands = RussoundCommands()
  }

  @Test fun testGetSource() {
    val zone0Bytes = "F000007F000070010402000002000077F7".toByteArray()
    assertThat(russoundCommands.requestSource(zone0)).isEqualTo(zone0Bytes)

    val zone1Bytes = "F000007F000070010402000102000078F7".toByteArray()
    assertThat(russoundCommands.requestSource(zone1)).isEqualTo(zone1Bytes)
  }

  @Test fun testCheckSum() {
    val bytes = "F000677CF10F00F7".toByteArray()
    assertThat(russoundCommands.calculateChecksum(bytes)).isEqualTo(0x59.toByte())
  }

  @Test fun testZone0PowerOn() {
    val powerOn = "F000007F0000700502020000F12300010000000112F7".toByteArray()
    assertThat(russoundCommands.turnOn(zone0)).isEqualTo(powerOn)
  }

  @Test fun testZone0PowerOff() {
    val powerOff = "F000007F0000700502020000F12300000000000111F7".toByteArray()
    assertThat(russoundCommands.turnOff(zone0)).isEqualTo(powerOff)
  }

  @Test fun testZone1PowerOn() {
    val powerOn = "F000007F0000700502020000F12300010001000113F7".toByteArray()
    assertThat(russoundCommands.turnOn(zone1)).isEqualTo(powerOn)
  }

  @Test fun testZone1PowerOff() {
    val powerOff = "F000007F0000700502020000F12300000001000112F7".toByteArray()
    assertThat(russoundCommands.turnOff(zone1)).isEqualTo(powerOff)
  }

  @Test fun testSource0Zone0() {
    val expected = "F000007F0000700502000000F13E0000000000012AF7".toByteArray()
    assertThat(russoundCommands.listen(toSource = source0, inZone = zone0)).isEqualTo(expected)
  }

  @Test fun testSource1Zone0() {
    val expected = "F000007F0000700502000000F13E0000000100012BF7".toByteArray()
    assertThat(russoundCommands.listen(toSource = source1, inZone = zone0)).isEqualTo(expected)
  }

  @Test fun testSource0Zone1() {
    val expected = "F000007F0001700502000000F13E0000000000012BF7".toByteArray()
    assertThat(russoundCommands.listen(toSource = source0, inZone = zone1)).isEqualTo(expected)
  }

  @Test fun testSource1Zone1() {
    val expected = "F000007F0001700502000000F13E0000000100012CF7".toByteArray()
    assertThat(russoundCommands.listen(toSource = source1, inZone = zone1)).isEqualTo(expected)
  }
}


private val HEX_CHARS = "0123456789ABCDEF"
private fun String.toByteArray() : ByteArray {
  val result = ByteArray(length / 2)

  for (i in 0 until length step 2) {
    val firstIndex = HEX_CHARS.indexOf(this[i]);
    val secondIndex = HEX_CHARS.indexOf(this[i + 1]);

    val octet = firstIndex.shl(4).or(secondIndex)
    result[i.shr(1)] = octet.toByte()
  }

  return result
}
