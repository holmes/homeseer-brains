package holmes.ponderosa.lights

import com.squareup.moshi.Moshi
import okio.Okio
import org.slf4j.LoggerFactory
import java.io.File
import java.io.InputStream
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Provider

private val LOG = LoggerFactory.getLogger(TwilightProvider::class.java)

class TwilightProvider(moshi: Moshi, private val now: Provider<LocalDate>, private val dataLocation: File) {
  private val adapter = moshi.adapter(SunriseSunsetProvider::class.java)
  private val twilightData = HashMap<LocalDate, TwilightResult>()

  companion object Factory {
    private val DEFAULT: TwilightResult = TwilightResult(LocalDate.now(),
        LocalTime.of(6, 30),
        LocalTime.of(7, 0),
        LocalTime.of(12, 30),
        LocalTime.of(18, 30),
        LocalTime.of(19, 0)
    )
  }

  fun refresh() {
    val now = now.get()

    // Trim old stuff first.
    twilightData
        .filterKeys { it.isBefore(now) }
        .forEach { date, _ -> twilightData.remove(date) }

    LOG.info("Loading twilight data from $dataLocation")
    loadData(now).apply { twilightData.put(date, this) }
    loadData(now.plusDays(1)).apply { twilightData.put(date, this) }
  }

  fun twilight(): TwilightResult {
    return twilightData.getOrElse(now.get()) {
      LOG.error("No twilight data for ${now.get()}. Did we really load it?")
      DEFAULT
    }
  }

  private fun loadData(date: LocalDate): TwilightResult {
    try {
      val inputStream = getFileStream(date)
      val bufferedSource = Okio.buffer(Okio.source(inputStream))
      val sunriseData = adapter.fromJson(bufferedSource)

      return TwilightResult(date,
          localTime(sunriseData.results.civil_twilight_begin),
          localTime(sunriseData.results.sunrise),
          localTime(sunriseData.results.solar_noon),
          localTime(sunriseData.results.sunset),
          localTime(sunriseData.results.civil_twilight_end)
      )
    } catch (e: RuntimeException) {
      LOG.error("Unable to load data for $date", e)
      return DEFAULT
    }
  }

  private fun getFileStream(date: LocalDate): InputStream {
    val yearValue = date.year
    val monthValue = date.monthValue
    val dayValue = date.dayOfMonth

    val fileName = "$yearValue-$monthValue-$dayValue.json"
    val actualFile = File(dataLocation, fileName)

    return actualFile.inputStream()
  }

  private fun localTime(dateString: String) =
      ZonedDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME)
          .withZoneSameInstant(ZoneId.of("America/Los_Angeles"))
          .toLocalTime()
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
