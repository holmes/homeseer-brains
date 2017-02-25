package holmes.ponderosa.lights

import com.google.common.truth.Truth.assertThat
import holmes.ponderosa.lights.TimeFrame.Factory.StaticTimeFrame
import org.junit.Before
import org.junit.Test
import java.time.LocalTime

class TimeFrameUnitTest {
  lateinit var now: LocalTime

  @Before fun setUp() {
    now = LocalTime.of(8, 0)
  }

  @Test fun testContainsTrue() {
    val timeFrame = StaticTimeFrame(now.plusHours(3), 12, 65)
    assertThat(timeFrame.contains(now)).isTrue()
  }

  @Test fun testContainsFalse() {
    val timeFrame = StaticTimeFrame(now.minusHours(3), 12, 65)
    assertThat(timeFrame.contains(now)).isFalse()
  }
}
