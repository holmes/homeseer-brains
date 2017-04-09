package holmes.ponderosa.audio.mock

import com.google.common.truth.Truth.assertThat
import holmes.ponderosa.audio.ZoneInfo
import holmes.ponderosa.util.toHexByteArray
import holmes.ponderosa.util.toHexString
import org.junit.Test

class RussoundMatrixToAppCommandsUnitTest {
  val zone1Info = ZoneInfo(1, 2, power = false, volume = 34, bass = 2, treble = 4, balance = -2, loudness = true)

  @Test fun testStatus() {
    val bytes = RussoundMatrixToAppCommands.returnStatus(zone1Info)
    assertThat(bytes).isEqualTo("F000007000007F00000402000107000001000C00000211F8FA01F4000000000014F7".toHexByteArray())
  }

  @Test fun testPower() {
    val bytes = RussoundMatrixToAppCommands.returnPower(zone1Info)
    assertThat(bytes).isEqualTo("F000007000007F000004020001060000010001000003F7".toHexByteArray())
  }

  @Test fun testVolume() {
    val bytes = RussoundMatrixToAppCommands.returnVolume(zone1Info)
    assertThat(bytes).isEqualTo("F000007000007F00000402000101000001000100110FF7".toHexByteArray())
  }

  @Test fun testSource() {
    val bytes = RussoundMatrixToAppCommands.returnSource(zone1Info)
    assertThat(bytes).isEqualTo("F000007000007F000004020001020000010001000201F7".toHexByteArray())
  }
}
