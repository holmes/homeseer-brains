package holmes.ponderosa.lights

import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.Moshi
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class TwilightProviderUnitTest {
  lateinit var twilightProvider: TwilightProvider

  @Before fun setUp() {
    val moshi = Moshi.Builder().build()
    twilightProvider = TwilightProvider(moshi, { LocalDate.of(2017, 2, 12) })
  }

  @Test fun testParsingDatesFromFile() {
    val twilight = twilightProvider.twilight()
    assertThat(twilight).isNotNull()

    assertThat(twilight.sunrise).isEquivalentAccordingToCompareTo(LocalTime.of(7, 0, 21))
    assertThat(twilight.sunset).isEquivalentAccordingToCompareTo(LocalTime.of(17, 44, 56))
  }
}
