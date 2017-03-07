package holmes.ponderosa.audio

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

data class ZoneInfo(val zone: Int, val source: Int, val power: Boolean = false, val volume: Int = 0, val bass: Int = 0, val treble: Int = 0, val balance: Int = 0, val loudness: Boolean = false) {
  fun copy(source: Int = this.source, power: Boolean = this.power, volume: Int = this.volume, bass: Int = this.bass, treble: Int = this.treble, balance: Int = this.balance, loudness: Boolean = this.loudness)
      = ZoneInfo(zone, source, power, volume, bass, treble, balance, loudness)
}

private val LOG = LoggerFactory.getLogger(AudioManager::class.java)

/**
 * The brains of the operation. Now we have to decide - send this information to Homeseer and
 * let it be the brains, or just let this store all the info.
 */
class AudioManager(private val zones: Zones,
                   private val audioCommander: AudioCommander,
                   receivedZoneInfo: Observable<RussoundAction>) {

  private val zoneInfoUpdates = PublishSubject.create<ZoneInfo>()
  private val allZoneInfo: MutableMap<Zone, ZoneInfo> = HashMap()

  val zoneInformation: Map<Zone, ZoneInfo>
    get() = allZoneInfo

  init {
    zones.all.values.forEach {
      allZoneInfo.put(it, ZoneInfo(zone = it.zoneNumber, source = 0))
    }

    // Subscribe for input from the receiver and re-publish updates to the ones listening here.
    receivedZoneInfo.subscribe(this::updateZoneFromStatus)
  }

  fun requestStatus(zone: Zone): ZoneInfo {
    audioCommander.requestStatus(zone)
    return allZoneInfo.getValue(zone)
  }

  fun power(zone: Zone, power: PowerChange): ZoneInfo {
    audioCommander.power(zone, power)
    return waitForStatusUpdate(zone)
  }

  fun source(zone: Zone, source: Source): ZoneInfo {
    audioCommander.changeSource(zone, source)
    return waitForStatusUpdate(zone)
  }

  /** Sets the initial volume when the zone is turned on. */
  fun initialVolume(zone: Zone, volume: Int) {
    // We don't pass this in the ZoneInformation. It's set once and we never care again.
    audioCommander.initialVolume(zone, volume)
  }

  fun volume(zone: Zone, volume: VolumeChange): ZoneInfo {
    audioCommander.volume(zone, volume)
    return waitForStatusUpdate(zone)
  }

  fun bass(zone: Zone, bass: BassLevel): ZoneInfo {
    audioCommander.bass(zone, bass)
    return waitForStatusUpdate(zone)
  }

  fun treble(zone: Zone, treble: TrebleLevel): ZoneInfo {
    audioCommander.treble(zone, treble)
    return waitForStatusUpdate(zone)
  }

  fun balance(zone: Zone, balance: Balance): ZoneInfo {
    audioCommander.balance(zone, balance)
    return waitForStatusUpdate(zone)
  }

  fun loudness(zone: Zone, loudness: Loudness): ZoneInfo {
    audioCommander.loudness(zone, loudness)
    return waitForStatusUpdate(zone)
  }

  /** Update the zone and request an update via AudioCommander */
  private fun waitForStatusUpdate(zone: Zone): ZoneInfo {
    audioCommander.requestStatus(zone)
    return zoneInfoUpdates
        .filter { it.zone == zone.zoneNumber }
        .timeout(200, TimeUnit.MILLISECONDS)
        .retry(2) {
          LOG.error("Didn't receive value in 500ms, sending request again.")
          audioCommander.requestStatus(zone)
          return@retry true
        }
        .onErrorReturn {
          LOG.error("Didn't receive zone update after retrying, giving up and using what we have.")
          allZoneInfo[zone]
        }
        .blockingFirst()
  }

  private fun updateZoneFromStatus(action: RussoundAction) {
    if (action is ReceivedStatusAction) {
      val zone = zones.zoneAt(action.zone)
      val currentZoneInfo = allZoneInfo[zone]

      if (currentZoneInfo != null) {
        val updatedInfo = action.applyTo(currentZoneInfo)
        allZoneInfo[zone] = updatedInfo
        zoneInfoUpdates.onNext(updatedInfo)
      } else {
        LOG.error("Couldn't find zone for ${action.description}")
      }
    }
  }
}
