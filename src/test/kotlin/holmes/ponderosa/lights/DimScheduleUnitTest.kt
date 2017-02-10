package holmes.ponderosa.lights

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import java.time.LocalTime

class DimScheduleUnitTest {
  lateinit var dimSchedule: DimSchedule
  lateinit var now: LocalTime

  @Before fun setUp() {
    val now = LocalTime.of(0, 0)

    val timeFrames = listOf(1, 2, 3, 4)
        .map { TimeFrame(now.plusHours((it * 5).toLong()), it * 10, it * 20) }

    dimSchedule = DimSchedule(22, timeFrames, { now })
  }

  @Test fun testAutoDimLightIsOff() {
    assertThat(dimSchedule.autoDim(0)).isEqualTo(-1)
  }

  @Test fun testAutoDimLightnFirstFrameIsLowLevel() {
    val firstFrame = dimSchedule.timeFrames[0]

    now = firstFrame.endTime.minusHours(1)
    assertThat(dimSchedule.autoDim(10)).isEqualTo(firstFrame.lowLevel)
  }
}
