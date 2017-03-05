package holmes.ponderosa.audio.mock

import holmes.ponderosa.audio.RussoundCommands
import holmes.ponderosa.audio.Zone
import org.junit.Before
import org.junit.Test

class RussoundMatrixToAppCommandsUnitTest {
  val matrixToAppCommands = RussoundMatrixToAppCommands()
  val appToMatrixCommands = RussoundCommands()

  @Before fun setUp() {

  }

  @Test fun testSomething() {
    val zone = Zone(0, 0, 1, "1")
    appToMatrixCommands.requestStatus(zone)

  }
}
