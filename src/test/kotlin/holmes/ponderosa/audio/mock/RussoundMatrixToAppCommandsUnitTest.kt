package holmes.ponderosa.audio.mock

import com.google.common.truth.Truth.assertThat
import holmes.ponderosa.audio.ZoneInfo
import holmes.ponderosa.util.toHexByteArray
import org.junit.Test

class RussoundMatrixToAppCommandsUnitTest {
  val matrixToAppCommands = RussoundMatrixToAppCommands()

  @Test fun testSomething() {
    val zone1Info = ZoneInfo(1, 2, power = false, volume = 33, bass = 2, treble = 4, balance = -2, loudness = true)
    val bytes = matrixToAppCommands.returnStatus(zone1Info)
    assertThat(bytes).isEqualTo("F000007000007F00000402000107000001000C00000210F8FA01F4000000000013F7".toHexByteArray())
  }
}
