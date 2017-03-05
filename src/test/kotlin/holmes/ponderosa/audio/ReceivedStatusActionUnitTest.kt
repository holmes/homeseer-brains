package holmes.ponderosa.audio

import com.google.common.truth.Truth.assertWithMessage
import com.nhaarman.mockito_kotlin.mock
import holmes.ponderosa.util.toHexByteArray
import org.junit.Test

class ReceivedStatusActionUnitTest {
  @Test fun testZone1GetStatus() {
    val expected = "F000007000007F00000402000007000001000C0001010A070A010A010000000045F7".toHexByteArray()

    val zoneInfo = ReceivedStatusAction(expected).handle(mock<ZoneInfo>())
    assertWithMessage("zoneId").that(zoneInfo.zone).isEqualTo(0)
    assertWithMessage("power").that(zoneInfo.power).isEqualTo(true)
    assertWithMessage("sourceId").that(zoneInfo.source).isEqualTo(1)
    assertWithMessage("volume").that(zoneInfo.volume).isEqualTo(20)
    assertWithMessage("bass").that(zoneInfo.bass).isEqualTo(-3)
    assertWithMessage("treble").that(zoneInfo.treble).isEqualTo(0)
    assertWithMessage("loudness").that(zoneInfo.loudness).isEqualTo(true)
    assertWithMessage("balance").that(zoneInfo.balance).isEqualTo(0)
  }

  @Test fun testZone2GetStatus() {
    val expected = "F000007000007F00000402000107000001000C0000010A0C0C000A01000000004BF7".toHexByteArray()

    val zoneInfo = ReceivedStatusAction(expected).handle(mock<ZoneInfo>())
    assertWithMessage("zoneId").that(zoneInfo.zone).isEqualTo(1)
    assertWithMessage("power").that(zoneInfo.power).isEqualTo(false)
    assertWithMessage("sourceId").that(zoneInfo.source).isEqualTo(1)
    assertWithMessage("volume").that(zoneInfo.volume).isEqualTo(20)
    assertWithMessage("bass").that(zoneInfo.bass).isEqualTo(2)
    assertWithMessage("treble").that(zoneInfo.treble).isEqualTo(2)
    assertWithMessage("loudness").that(zoneInfo.loudness).isEqualTo(false)
    assertWithMessage("balance").that(zoneInfo.balance).isEqualTo(0)
  }
}
