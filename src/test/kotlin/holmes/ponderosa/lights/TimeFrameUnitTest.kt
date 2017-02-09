package holmes.ponderosa.lights

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.util.*

class TimeFrameUnitTest {

  @Test fun testContainsTrue() {
    val timeFrame = TimeFrame(Date(System.currentTimeMillis() + 10000), 12, 65)
    assertThat(timeFrame.contains(Date())).isTrue()
  }

  @Test fun testContainsFalse() {
    val timeFrame = TimeFrame(Date(System.currentTimeMillis() - 10000), 12, 65)
    assertThat(timeFrame.contains(Date())).isFalse()
  }
}
