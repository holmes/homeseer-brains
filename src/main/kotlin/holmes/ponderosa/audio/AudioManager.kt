package holmes.ponderosa.audio

import io.reactivex.Observable
import org.holmes.russound.Balance
import org.holmes.russound.BassLevel
import org.holmes.russound.Loudness
import org.holmes.russound.PowerChange
import org.holmes.russound.RussoundCommander
import org.holmes.russound.TrebleLevel
import org.holmes.russound.VolumeChange
import org.holmes.russound.ZoneInfo
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

private val LOG = LoggerFactory.getLogger(AudioManager::class.java)

/**
 * The brains of the operation. Now we have to decide - send this information to Homeseer and
 * let it be the brains, or just let this store all the info.
 */
class AudioManager(private val zones: Zones,
                   private val audioCommander: RussoundCommander,
                   private val receivedZoneInfo: Observable<ZoneInfo>) {

  private val zoneConfigs: MutableMap<ZoneConfig, ZoneInfo> = HashMap()

  val zoneInformation: Map<ZoneConfig, ZoneInfo>
    get() = zoneConfigs

  init {
    zones.all.values.forEach {
      zoneConfigs.put(it, ZoneInfo(zone = it.zoneNumber, source = 0))
    }

    // Subscribe for input from the receiver and re-publish updates to the ones listening here.
    receivedZoneInfo.subscribe(this::updateZoneFromStatus)
  }

  fun requestStatus(zoneConfig: ZoneConfig): ZoneInfo {
    return waitForStatusUpdate(zoneConfig)
  }

  fun power(zoneConfig: ZoneConfig, power: PowerChange): ZoneInfo {
    audioCommander.power(zoneConfig.zone, power)
    return waitForStatusUpdate(zoneConfig)
  }

  fun source(zoneConfig: ZoneConfig, sourceConfig: SourceConfig): ZoneInfo {
    audioCommander.source(zoneConfig.zone, sourceConfig.source)
    return waitForStatusUpdate(zoneConfig)
  }

  /** Sets the initial volume when the zoneConfig is turned on. */
  fun initialVolume(zoneConfig: ZoneConfig, volume: Int) {
    // We don't pass this in the ZoneInformation. It's set once and we never care again.
    audioCommander.initialVolume(zoneConfig.zone, volume)
  }

  fun volume(zoneConfig: ZoneConfig, volume: VolumeChange): ZoneInfo {
    audioCommander.volume(zoneConfig.zone, volume)
    return waitForStatusUpdate(zoneConfig)
  }

  fun bass(zoneConfig: ZoneConfig, bass: BassLevel): ZoneInfo {
    audioCommander.bass(zoneConfig.zone, bass)
    return waitForStatusUpdate(zoneConfig)
  }

  fun treble(zoneConfig: ZoneConfig, treble: TrebleLevel): ZoneInfo {
    audioCommander.treble(zoneConfig.zone, treble)
    return waitForStatusUpdate(zoneConfig)
  }

  fun balance(zoneConfig: ZoneConfig, balance: Balance): ZoneInfo {
    audioCommander.balance(zoneConfig.zone, balance)
    return waitForStatusUpdate(zoneConfig)
  }

  fun loudness(zoneConfig: ZoneConfig, loudness: Loudness): ZoneInfo {
    audioCommander.loudness(zoneConfig.zone, loudness)
    return waitForStatusUpdate(zoneConfig)
  }

  /** Requests and waits for a ZoneInfo status update. */
  private fun waitForStatusUpdate(zoneConfig: ZoneConfig): ZoneInfo {
    audioCommander.requestStatus(zoneConfig.zone)
    return receivedZoneInfo
        .filter { it.zone == zoneConfig.zoneNumber }
        .timeout(200, TimeUnit.MILLISECONDS)
        .retry(2) {
          LOG.error("Didn't receive value in 500ms, sending request again.")
          audioCommander.requestStatus(zoneConfig.zone)
          return@retry true
        }
        .onErrorReturn {
          LOG.error("Didn't receive zoneConfig update after retrying, giving up and using what we have.")
          zoneConfigs[zoneConfig]
        }
        .blockingFirst()
  }

  /** When we get ZoneInfo, we always need to update our local information. */
  private fun updateZoneFromStatus(zoneInfo: ZoneInfo) {
    val zone = zones.zoneAt(zoneInfo.zone)
    val currentZoneInfo = zoneConfigs[zone]

    if (currentZoneInfo != null) {
      zoneConfigs[zone] = zoneInfo
    } else {
      LOG.error("Couldn't find zone for $zoneInfo")
    }
  }
}
