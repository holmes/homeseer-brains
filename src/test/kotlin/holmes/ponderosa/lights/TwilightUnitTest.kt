package holmes.ponderosa.lights

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import java.util.*

class TwilightUnitTest {
  lateinit var twilightResult: TwilightResult
  lateinit var twilight: Twilight

  @Before fun setUp() {
    val cal = Calendar.getInstance()

    // Sunrise is at 7:21
    cal.set(2017, 2, 8, 7, 21)
    val sunrise = cal.time

    // Solar Noon is at 12:26
    cal.add(Calendar.HOUR, 5)
    cal.add(Calendar.MINUTE, 5)
    val solarNoon = cal.time

    // Sunset is at 5:41
    cal.add(Calendar.HOUR, 5)
    cal.add(Calendar.MINUTE, 15)
    val sunset = cal.time

    twilightResult = TwilightResult(sunrise, sunset, solarNoon)
    twilight = Twilight(resultsProvider = { twilightResult })
  }

  @Test fun testSunrise() {
    val sunrise = twilight.sunrise()

    val calendar = Calendar.getInstance()
    calendar.time = sunrise

    assertThat(calendar.get(Calendar.HOUR_OF_DAY)).isEqualTo(7)
    assertThat(calendar.get(Calendar.MINUTE)).isEqualTo(21)
  }

  @Test fun testSunriseOffset() {
    val sunrise = twilight.sunrise(-30)

    val calendar = Calendar.getInstance()
    calendar.time = sunrise

    assertThat(calendar.get(Calendar.HOUR)).isEqualTo(6)
    assertThat(calendar.get(Calendar.MINUTE)).isEqualTo(51)
  }

  @Test fun testSolarNoon() {
    val solarNoon = twilight.solarNoon()
    val calendar = Calendar.getInstance()
    calendar.time = solarNoon

    assertThat(calendar.get(Calendar.HOUR_OF_DAY)).isEqualTo(12)
    assertThat(calendar.get(Calendar.MINUTE)).isEqualTo(26)
  }

  @Test fun testSolarNoonOffset() {
    val sunrise = twilight.solarNoon(-30)

    val calendar = Calendar.getInstance()
    calendar.time = sunrise

    assertThat(calendar.get(Calendar.HOUR_OF_DAY)).isEqualTo(11)
    assertThat(calendar.get(Calendar.MINUTE)).isEqualTo(56)
  }

  @Test fun testSunset() {
    val sunrise = twilight.sunset()

    val calendar = Calendar.getInstance()
    calendar.time = sunrise

    assertThat(calendar.get(Calendar.HOUR)).isEqualTo(5)
    assertThat(calendar.get(Calendar.MINUTE)).isEqualTo(41)
  }

  @Test fun testSunsetOffset() {
    val sunrise = twilight.sunset(30)

    val calendar = Calendar.getInstance()
    calendar.time = sunrise

    assertThat(calendar.get(Calendar.HOUR)).isEqualTo(6)
    assertThat(calendar.get(Calendar.MINUTE)).isEqualTo(11)
  }
}
