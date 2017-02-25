package holmes.ponderosa.lights

import org.slf4j.LoggerFactory
import java.util.Timer
import java.util.concurrent.TimeUnit

private val LOG = LoggerFactory.getLogger(TwilightDataRefresher::class.java)

class TwilightDataRefresher(val twilightProvider: TwilightProvider) {
  var timer: Timer? = null

  fun start() {
    val initialDelay = TimeUnit.SECONDS.toMillis(5)
    val period = TimeUnit.HOURS.toMillis(8)

    LOG.info("Starting twilight data refreshing in ${initialDelay / 1000}s")
    timer = kotlin.concurrent.fixedRateTimer("twilght-data-refresher", true, initialDelay, period) {
      LOG.info("It's time to refresh twilight data")
      twilightProvider.refresh()
    }
  }

  fun stop() {
    timer?.cancel()
  }
}
