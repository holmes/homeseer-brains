package holmes.ponderosa.lights

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import java.time.LocalTime

class DimScheduleUnitTest {
  lateinit var dimSchedule: DimSchedule
  lateinit var now: LocalTime

  @Before fun setUp() {
    val midnight = LocalTime.of(0, 0)
    val timeFrames = listOf(
      TimeFrame(midnight.plusHours(7), 1, 35),
      TimeFrame(midnight.plusHours(8), 1, 35),
      TimeFrame(midnight.plusHours(13), 30, 85),
      TimeFrame(midnight.plusHours(23).plusMinutes(59), 1, 35)
    )

    dimSchedule = DimSchedule(22, timeFrames) { now }
  }

  @Test fun testAutoDimLightIsOff() {
    assertThat(dimSchedule.autoDim(0)).isEqualTo(-1)
  }

  @Test fun testDoNotAutoDimLightInFirstFrame() {
    val firstFrame = dimSchedule.timeFrames[0]

    now = firstFrame.endTime.minusHours(1)
    assertThat(dimSchedule.autoDim(10)).isEqualTo(-1)
  }

  @Test fun testAutoDimLightAtVeryLowLevelDoNothing() {
    val currentFrame = dimSchedule.timeFrames[1]

    now = currentFrame.endTime.minusHours(1)
    assertThat(dimSchedule.autoDim(1)).isEqualTo(-1)
  }

  @Test fun testAutoDimOverHighLevelDoNothing() {
    val currentFrame = dimSchedule.timeFrames[2]

    now = currentFrame.endTime.minusHours(1)
    assertThat(dimSchedule.autoDim(88)).isEqualTo(-1)
  }

  @Test fun testAutoDimLightNearCurrentLevelDims() {
    val testFrame = dimSchedule.timeFrames[2]
    val currentValue = 27

    now = testFrame.endTime.minusMinutes(10)
    assertThat(dimSchedule.autoDim(currentValue)).isEqualTo(29)
  }

  @Test fun testAutoDimLighNottNearCurrentDoNothing() {
    val testFrame = dimSchedule.timeFrames[2]
    val currentValue = testFrame.lowLevel + 1

    now = testFrame.endTime.minusMinutes(10)
    assertThat(dimSchedule.autoDim(currentValue)).isEqualTo(-1)
  }

  @Test fun testAutoDimForReals() {
    val currentFrame = dimSchedule.timeFrames[2]

    now = currentFrame.endTime.minusMinutes(10)
    assertThat(dimSchedule.autoDim(28)).isEqualTo(29)
  }

  @Test fun testAutoDimForSameValue() {
    val currentFrame = dimSchedule.timeFrames[2]

    now = currentFrame.endTime.minusMinutes(10)
    assertThat(dimSchedule.autoDim(29)).isEqualTo(29)
  }

  @Test fun testPreviousFrame() {
    val previousFrame = dimSchedule.timeFrames[1]
    val testingFrame = dimSchedule.timeFrames[2]

    now = testingFrame.endTime.minusHours(1)
    assertThat(dimSchedule.previousFrame).isSameAs(previousFrame)
  }

  @Test fun testCurrentFrame() {
    val testingFrame = dimSchedule.timeFrames[2]
    now = testingFrame.endTime.minusHours(1)
    assertThat(dimSchedule.currentFrame).isSameAs(testingFrame)
  }

}
