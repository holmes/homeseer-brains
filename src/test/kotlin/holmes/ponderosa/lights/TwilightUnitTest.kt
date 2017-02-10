package holmes.ponderosa.lights

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import java.time.LocalTime

class TwilightUnitTest {
  lateinit var twilightResult: TwilightResult
  lateinit var twilight: Twilight

  @Before fun setUp() {
    // Sunrise is at 7:21
    val sunrise = LocalTime.of(7, 21)

    // Solar Noon is at 12:26
    val solarNoon = sunrise.plusHours(5).plusMinutes(5)

    // Sunset is at 5:41
    val sunset = solarNoon.plusHours(5).plusMinutes(15)

    twilightResult = TwilightResult(sunrise, sunset, solarNoon)
    twilight = Twilight(resultsProvider = { twilightResult })
  }

  @Test fun testSunrise() {
    val sunrise = twilight.sunrise()
    assertThat(sunrise.hour).isEqualTo(7)
    assertThat(sunrise.minute).isEqualTo(21)
  }

  @Test fun testSunriseOffset() {
    val sunrise = twilight.sunrise(-30)
    assertThat(sunrise.hour).isEqualTo(6)
    assertThat(sunrise.minute).isEqualTo(51)
  }

  @Test fun testSolarNoon() {
    val solarNoon = twilight.solarNoon()

    assertThat(solarNoon.hour).isEqualTo(12)
    assertThat(solarNoon.minute).isEqualTo(26)
  }

  @Test fun testSolarNoonOffset() {
    val solarNoon = twilight.solarNoon(-30)
    assertThat(solarNoon.hour).isEqualTo(11)
    assertThat(solarNoon.minute).isEqualTo(56)
  }

  @Test fun testSunset() {
    val sunset = twilight.sunset()
    assertThat(sunset.hour).isEqualTo(17)
    assertThat(sunset.minute).isEqualTo(41)
  }

  @Test fun testSunsetOffset() {
    val sunset = twilight.sunset(30)
    assertThat(sunset.hour).isEqualTo(18)
    assertThat(sunset.minute).isEqualTo(11)
  }
}
