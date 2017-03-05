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

    LOG.info("Starting timer to request audio status in ${initialDelay / 1000}s")
    timer = timer("audio-status-request-timer", true, initialDelay, period) {
      LOG.info("It's time to request audio status")

      if (firstRun) {
        // The matrix doesn't seem to respond to the very first request.
        // It's like it needs to be primed or something.
        firstRun = false
        audioManager.requestStatus(zones.zoneAt(0))
      }

      zones.all.values.forEach { zone ->
        audioManager.requestStatus(zone)
      }
    }
  }

  fun stop() {
    timer?.cancel()
  }
}
