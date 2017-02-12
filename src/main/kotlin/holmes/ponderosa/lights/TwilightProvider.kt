package holmes.ponderosa.lights

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import okio.Okio
import java.io.InputStream
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class TwilightProvider(val moshi: Moshi, val now: () -> LocalDate) {
  companion object Factory {
    private val DEFAULT: TwilightResult = TwilightResult(
        LocalTime.of(6, 30),
        LocalTime.of(7, 0),
        LocalTime.of(1, 0),
        LocalTime.of(18, 30),
        LocalTime.of(19, 0)
    )
  }

  fun twilight(): TwilightResult {
    try {
      val inputStream = getFileStream(now())
      val bufferedSource = Okio.buffer(Okio.source(inputStream))

      val adapter: JsonAdapter<SunriseSunsetProvider> = moshi.adapter(SunriseSunsetProvider::class.java)
      val sunriseSunsetProvider = adapter.fromJson(bufferedSource)

      return TwilightResult(
          localTime(sunriseSunsetProvider.results.civil_twilight_begin),
          localTime(sunriseSunsetProvider.results.sunrise),
          localTime(sunriseSunsetProvider.results.solar_noon),
          localTime(sunriseSunsetProvider.results.sunset),
          localTime(sunriseSunsetProvider.results.civil_twilight_end)
      )
    } catch (e: RuntimeException) {
      return DEFAULT
    }
  }

  private fun localTime(dateString: String) =
      ZonedDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME)
          .withZoneSameInstant(ZoneId.of("America/Los_Angeles"))
          .toLocalTime()

  private fun getFileStream(today: LocalDate): InputStream {
    val yearValue = today.year
    val monthValue = today.monthValue
    val dayValue = today.dayOfMonth

    val fileName = "$yearValue-$monthValue-$dayValue.json"
    val fileStream = TwilightProvider::class.java.classLoader.getResourceAsStream(fileName)

    return fileStream
  }
}

private class SunriseSunsetProvider {
  class Results {
    lateinit var civil_twilight_begin: String
    lateinit var sunrise: String
    lateinit var solar_noon: String
    lateinit var sunset: String
    lateinit var civil_twilight_end: String
  }

  lateinit var results: Results
}
