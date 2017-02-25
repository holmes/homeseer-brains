package holmes.ponderosa.lights

import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.Moshi
import org.junit.Before
import org.junit.Test
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Provider

class TwilightProviderUnitTest {
  lateinit var twilightProvider: TwilightProvider

  @Before fun setUp() {
    val moshi = Moshi.Builder().build()
    val testDate = LocalDate.of(2017, 2, 12)

    twilightProvider = TwilightProvider(moshi, Provider { testDate }, File("src/test/resources"))
    twilightProvider.refresh()
  }

  @Test fun testParsingDatesFromFile() {
    val twilight = twilightProvider.twilight()
    assertThat(twilight).isNotNull()

    assertThat(twilight.twilightBegin).isEquivalentAccordingToCompareTo(LocalTime.of(6, 33, 27))
    assertThat(twilight.sunrise).isEquivalentAccordingToCompareTo(LocalTime.of(7, 0, 21))
    assertThat(twilight.solarNoon).isEquivalentAccordingToCompareTo(LocalTime.of(12, 22, 39))
    assertThat(twilight.sunset).isEquivalentAccordingToCompareTo(LocalTime.of(17, 44, 56))
    assertThat(twilight.twilightEnd).isEquivalentAccordingToCompareTo(LocalTime.of(18, 11, 51))
  }
}
