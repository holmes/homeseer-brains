package holmes.ponderosa.audio

import org.slf4j.LoggerFactory
import java.util.Timer
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer

private val LOG = LoggerFactory.getLogger(AudioStatusRequestTimer::class.java)

class AudioStatusRequestTimer(val zones: Zones, val audioManager: AudioManager) {
  var timer: Timer? = null
  var firstRun = true

  fun start() {
    val initialDelay = TimeUnit.SECONDS.toMillis(5)
    val period = TimeUnit.MINUTES.toMillis(5)

    LOG.info("Starting audio status-requests in ${initialDelay / 1000}s")
    timer = timer("audio-status-requestor", true, initialDelay, period) {
      LOG.info("It's time to request audio status")

      if (firstRun) {
        // The matrix doesn't seem to respond to the very first request.
        // It's like it needs to be primed or something.
        firstRun = false
        audioManager.requestStatus(zones.zone(0))
        Thread.sleep(5000)
      }

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
