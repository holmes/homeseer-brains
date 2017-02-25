package holmes.ponderosa.audio

import org.slf4j.LoggerFactory
import java.util.Timer
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer

private val LOG = LoggerFactory.getLogger(StatusRequestTimer::class.java)

class StatusRequestTimer(val zones: Zones, val audioManager: AudioManager) {
  var timer: Timer? = null

  fun start() {
    val initialDelay = TimeUnit.SECONDS.toMillis(5)
    val period = TimeUnit.MINUTES.toMillis(5)

    LOG.info("Starting status-requests in ${initialDelay / 1000}s")
    timer = timer("audio-status-requestor", true, initialDelay, period) {
      LOG.info("It's time to request status")
      zones.all.values.forEach { zone ->
        audioManager.requestStatus(zone)
        Thread.sleep(2000)
      }
    }
  }

  fun stop() {
    timer?.cancel()
  }
}
