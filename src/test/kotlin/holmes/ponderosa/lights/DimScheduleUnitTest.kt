package holmes.ponderosa.lights

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import java.time.LocalTime
import javax.inject.Provider

class DimScheduleUnitTest {
  lateinit var dimSchedule: DimSchedule
  lateinit var zone: LightZone
  lateinit var now: LocalTime

  @Before fun setUp() {
    val midnight = LocalTime.of(0, 0)
    val timeFrames = listOf(
        TimeFrame(midnight.plusHours(7), 1, 35),
        TimeFrame(midnight.plusHours(8), 1, 35),
        TimeFrame(midnight.plusHours(13), 30, 85),
        TimeFrame(midnight.plusHours(23).plusMinutes(59), 1, 35)
    )

    zone = LightZone(11, setOf(), timeFrames)
    dimSchedule = DimSchedule(Provider { now })
  }

  @Test fun testAutoDimLightIsOff() {
    assertThat(dimSchedule.autoDim(zone, 0).dimLevel).isEqualTo(-1)
    assertThat(dimSchedule.autoDim(zone, 0).needsReschedule).isFalse()
  }

  @Test fun testDoNotAutoDimLightInFirstFrame() {
    val firstFrame = zone.timeFrames[0]
    now = firstFrame.endTime.minusHours(1)

    assertThat(dimSchedule.autoDim(zone, 10).dimLevel).isEqualTo(-1)
    assertThat(dimSchedule.autoDim(zone, 10).needsReschedule).isFalse()
  }

  @Test fun testAutoDimLightAtVeryLowLevelDoNothing() {
    val currentFrame = zone.timeFrames[1]
    now = currentFrame.endTime.minusHours(1)

    assertThat(dimSchedule.autoDim(zone, 1).dimLevel).isEqualTo(1)
    assertThat(dimSchedule.autoDim(zone, 1).needsReschedule).isFalse()
  }

  @Test fun testAutoDimOverHighLevelDoNothing() {
    val currentFrame = zone.timeFrames[2]
    now = currentFrame.endTime.minusHours(1)

    assertThat(dimSchedule.autoDim(zone, 88).dimLevel).isEqualTo(-1)
    assertThat(dimSchedule.autoDim(zone, 88).needsReschedule).isFalse()
  }

  @Test fun testAutoDimLightNearCurrentLevelDims() {
    val testFrame = zone.timeFrames[2]
    now = testFrame.endTime.minusMinutes(10)

    assertThat(dimSchedule.autoDim(zone, 28).dimLevel).isEqualTo(29)
    assertThat(dimSchedule.autoDim(zone, 28).needsReschedule).isFalse()

    // Close ones need rescheduling.
    assertThat(dimSchedule.autoDim(zone, 27).needsReschedule).isTrue()
    assertThat(dimSchedule.autoDim(zone, 26).needsReschedule).isTrue()
    assertThat(dimSchedule.autoDim(zone, 25).needsReschedule).isFalse()
  }

  @Test fun testAutoDimLightNotNearCurrentDoNothing() {
    val testFrame = zone.timeFrames[2]
    val currentValue = testFrame.lowLevel + 10

    now = testFrame.endTime.minusMinutes(10)
    assertThat(dimSchedule.autoDim(zone, currentValue).dimLevel).isEqualTo(-1)
    assertThat(dimSchedule.autoDim(zone, currentValue).needsReschedule).isFalse()
  }

  @Test fun testAutoDimForReal() {
    val currentFrame = zone.timeFrames[2]

    now = currentFrame.endTime.minusMinutes(10)
    assertThat(dimSchedule.autoDim(zone, 28).dimLevel).isEqualTo(29)
    assertThat(dimSchedule.autoDim(zone, 28).needsReschedule).isFalse()
  }

  @Test fun testAutoDimForSameValue() {
    val currentFrame = zone.timeFrames[2]

    now = currentFrame.endTime.minusMinutes(10)
    assertThat(dimSchedule.autoDim(zone, 29).dimLevel).isEqualTo(29)
    assertThat(dimSchedule.autoDim(zone, 29).needsReschedule).isFalse()
  }

  @Test fun testToggleLightsWhenOff() {
    val currentFrame = zone.timeFrames[2]

    now = currentFrame.endTime.minusMinutes(100)
    val result = dimSchedule.toggleLights(zone, 0).results.first().value
    assertThat(result).isEqualTo(20)
  }

  @Test fun testToggleLightsWhenWellBelowLevel() {
    val currentFrame = zone.timeFrames[2]

    now = currentFrame.endTime.minusMinutes(100)
    val result = dimSchedule.toggleLights(zone, 6).results.first().value
    assertThat(result).isEqualTo(currentFrame.highLevel)
  }

  @Test fun testToggleLightsWhenJustBelowLevel() {
    val currentFrame = zone.timeFrames[2]

    now = currentFrame.endTime.minusMinutes(100)
    val result = dimSchedule.toggleLights(zone, 18).results.first().value
    assertThat(result).isEqualTo(currentFrame.highLevel)
  }

  @Test fun testToggleLightsWhenAtLevel() {
    val currentFrame = zone.timeFrames[2]

    now = currentFrame.endTime.minusMinutes(100)
    val result = dimSchedule.toggleLights(zone, 20).results.first().value
    assertThat(result).isEqualTo(currentFrame.highLevel)
  }

  @Test fun testToggleLightsWhenJustAboveLevel() {
    val currentFrame = zone.timeFrames[2]

    now = currentFrame.endTime.minusMinutes(100)
    val result = dimSchedule.toggleLights(zone, 23).results.first().value
    assertThat(result).isEqualTo(currentFrame.highLevel)
  }

  @Test fun testToggleLightsWhenNearHigh() {
    val currentFrame = zone.timeFrames[2]

    now = currentFrame.endTime.minusMinutes(100)
    val result = dimSchedule.toggleLights(zone, currentFrame.highLevel - 2).results.first().value
    assertThat(result).isEqualTo(20)
  }

  @Test fun testToggleLightsWhenOnHigh() {
    val currentFrame = zone.timeFrames[2]

    now = currentFrame.endTime.minusMinutes(100)
    val result = dimSchedule.toggleLights(zone, currentFrame.highLevel).results.first().value
    assertThat(result).isEqualTo(20)
  }

  @Test fun testToggleLightsWhenAboveHigh() {
    val currentFrame = zone.timeFrames[2]

    now = currentFrame.endTime.minusMinutes(100)
    val result = dimSchedule.toggleLights(zone, currentFrame.highLevel + 7).results.first().value
    assertThat(result).isEqualTo(20)
  }

  @Test fun testIsInLowLevel() {
    now = zone.timeFrames[0].endTime.minusMinutes(10)
    assertThat(dimSchedule.isInLowLevel(zone, 1)).isTrue()
    assertThat(dimSchedule.isInLowLevel(zone, 10)).isFalse()

    now = zone.timeFrames[2].endTime.minusMinutes(10)
    assertThat(dimSchedule.isInLowLevel(zone, 10)).isTrue()
    assertThat(dimSchedule.isInLowLevel(zone, 55)).isFalse()

    now = zone.timeFrames[3].endTime.minusMinutes(10)
    assertThat(dimSchedule.isInLowLevel(zone, 10)).isTrue()
    assertThat(dimSchedule.isInLowLevel(zone, 55)).isFalse()
  }

  @Test fun testPreviousFrame() {
    val previousFrame = zone.timeFrames[1]
    val testingFrame = zone.timeFrames[2]

    now = testingFrame.endTime.minusHours(1)
    assertThat(zone.previousFrame(Provider { now })).isSameAs(previousFrame)
  }

  @Test fun testCurrentFrame() {
    val testingFrame = zone.timeFrames[2]
    now = testingFrame.endTime.minusHours(1)
    assertThat(zone.currentFrame(Provider { now })).isSameAs(testingFrame)
  }
}
