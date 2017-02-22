package holmes.ponderosa.audio

import com.google.common.truth.Truth.assertThat
import holmes.ponderosa.util.toHexByteArray
import org.junit.Before
import org.junit.Test

class RussoundCommandsUnitTest {
  lateinit var russoundCommands: RussoundCommands
  lateinit var zone1: Zone
  lateinit var zone2: Zone
  lateinit var source0: Source
  lateinit var source1: Source

  @Before fun setUp() {
    zone1 = Zone(0, 12, 1, "Kitchen")
    zone2 = Zone(0, 15, 2, "Outside")
    source0 = Source(0, 0, 1, "TV Family Room")
    source1 = Source(0, 1, 2, "Chromecast")
    russoundCommands = RussoundCommands()
  }

  @Test fun testZone1GetStatus() {
    val expected = "F000007F00007001040200000700007CF7".toHexByteArray()
    assertThat(russoundCommands.requestStatus(zone1)).isEqualTo(expected)
  }

  @Test fun testZone2GetStatus() {
    val expected = "F000007F00007001040200010700007DF7".toHexByteArray()
    assertThat(russoundCommands.requestStatus(zone2)).isEqualTo(expected)
  }

  @Test fun testZone1PowerOn() {
    val expected = "F000007F0000700502020000F12300010000000112F7".toHexByteArray()
    assertThat(russoundCommands.turnOn(zone1)).isEqualTo(expected)
  }

  @Test fun testZone2PowerOn() {
    val expected = "F000007F0000700502020000F12300010001000113F7".toHexByteArray()
    assertThat(russoundCommands.turnOn(zone2)).isEqualTo(expected)
  }

  @Test fun testZone1PowerOff() {
    val expected = "F000007F0000700502020000F12300000000000111F7".toHexByteArray()
    assertThat(russoundCommands.turnOff(zone1)).isEqualTo(expected)
  }

  @Test fun testZone2PowerOff() {
    val expected = "F000007F0000700502020000F12300000001000112F7".toHexByteArray()
    assertThat(russoundCommands.turnOff(zone2)).isEqualTo(expected)
  }

  @Test fun testZone1VolumeUp() {
    val expected = "F000007F00007005020200007F0000000000017BF7".toHexByteArray()
    assertThat(russoundCommands.volumeUp(zone1)).isEqualTo(expected)
  }

  @Test fun testZone2VolumeUp() {
    val expected = "F000007F00017005020200007F0000000000017CF7".toHexByteArray()
    assertThat(russoundCommands.volumeUp(zone2)).isEqualTo(expected)
  }

  @Test fun testZone1VolumeDown() {
    val expected = "F000007F0000700502020000F17F0000000000016DF7".toHexByteArray()
    assertThat(russoundCommands.volumeDown(zone1)).isEqualTo(expected)
  }

  @Test fun testZone2VolumeDown() {
    val expected = "F000007F0001700502020000F17F0000000000016EF7".toHexByteArray()
    assertThat(russoundCommands.volumeDown(zone2)).isEqualTo(expected)
  }

  @Test fun testZone1VolumeSet() {
    var expected = "F000007F0000700502020000F1210000000000010FF7".toHexByteArray()
    assertThat(russoundCommands.volume(zone1, 0)).isEqualTo(expected)

    expected = "F000007F0000700502020000F121000B000000011AF7".toHexByteArray()
    assertThat(russoundCommands.volume(zone1, 22)).isEqualTo(expected)

    expected = "F000007F0000700502020000F12100140000000123F7".toHexByteArray()
    assertThat(russoundCommands.volume(zone1, 40)).isEqualTo(expected)

    expected = "F000007F0000700502020000F12100320000000141F7".toHexByteArray()
    assertThat(russoundCommands.volume(zone1, 100)).isEqualTo(expected)
  }

  @Test fun testZone2VolumeSt() {
    var expected = "F000007F0000700502020000F12100000001000110F7".toHexByteArray()
    assertThat(russoundCommands.volume(zone2, 0)).isEqualTo(expected)

    expected = "F000007F0000700502020000F121000B000100011BF7".toHexByteArray()
    assertThat(russoundCommands.volume(zone2, 22)).isEqualTo(expected)

    expected = "F000007F0000700502020000F12100140001000124F7".toHexByteArray()
    assertThat(russoundCommands.volume(zone2, 40)).isEqualTo(expected)

    expected = "F000007F0000700502020000F12100320001000142F7".toHexByteArray()
    assertThat(russoundCommands.volume(zone2, 100)).isEqualTo(expected)
  }

  @Test fun testSource0Zone1() {
    val expected = "F000007F0000700502000000F13E0000000000012AF7".toHexByteArray()
    assertThat(russoundCommands.listen(zone = zone1, source = source0)).isEqualTo(expected)
  }

  @Test fun testSource1Zone1() {
    val expected = "F000007F0000700502000000F13E0000000100012BF7".toHexByteArray()
    assertThat(russoundCommands.listen(zone = zone1, source = source1)).isEqualTo(expected)
  }

  @Test fun testSource0Zone2() {
    val expected = "F000007F0001700502000000F13E0000000000012BF7".toHexByteArray()
    assertThat(russoundCommands.listen(zone = zone2, source = source0)).isEqualTo(expected)
  }

  @Test fun testSource1Zone2() {
    val expected = "F000007F0001700502000000F13E0000000100012CF7".toHexByteArray()
    assertThat(russoundCommands.listen(zone = zone2, source = source1)).isEqualTo(expected)
  }

  @Test fun testTurnOnVolumeZone1() {
    val expected = "F0 00 00 7F 00 00 70 00 05 02 00 00 00 04 00 00 00 01 00 01 00 0B 0D F7".replace(" ", "") .toHexByteArray()
    assertThat(russoundCommands.turnOnVolume(zone1, 22)).isEqualTo(expected)
  }

  @Test fun testTurnOnVolumeZone2() {
    val expected = "F000007F00007000050200010004000000010001001619F7".toHexByteArray()
    assertThat(russoundCommands.turnOnVolume(zone2, 44)).isEqualTo(expected)
  }
}
