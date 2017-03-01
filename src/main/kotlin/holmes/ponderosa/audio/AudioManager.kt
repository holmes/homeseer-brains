package holmes.ponderosa.audio

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

data class ZoneInfo(val zone: Zone, val source: Source?, val power: Boolean = false, val volume: Int = 0, val bass: Int = 0, val treble: Int = 0, val balance: Int = 0, val loudness: Boolean = false)

private val LOG = LoggerFactory.getLogger(AudioManager::class.java)

/**
 * The brains of the operation. Now we have to decide - send this information to Homeseer and
 * let it be the brains, or just let this store all the info.
 */
class AudioManager(private val zones: Zones, private val sources: Sources,
                   private val audioCommander: AudioCommander,
                   receivedZoneInfo: Observable<ReceivedZoneInfo>) {

  private val zoneInfoUpdates = PublishSubject.create<ZoneInfo>()
  private val allZoneInfo: MutableMap<Zone, ZoneInfo> = HashMap()

  val zoneInformation: Map<Zone, ZoneInfo>
    get() = allZoneInfo

  init {
    zones.all.values.forEach {
      allZoneInfo.put(it, ZoneInfo(zone = it, source = null))
    }

    // Subscribe for input from the receiver and re-publish updates to the ones listening here.
    receivedZoneInfo.subscribe(this::updateZoneFromStatus)
  }

  fun requestStatus(zone: Zone): ZoneInfo {
    audioCommander.requestStatus(zone)
    return zoneInformation.getValue(zone)
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
    val oldZone = allZoneInfo.getValue(zone)

    if (oldZone.loudness != loudness.isOn) {
      audioCommander.loudness(zone)
      return waitForStatusUpdate(zone)
    } else {
      return oldZone
    }
  }

  /** Update the zone and request an update via AudioCommander */
  private fun waitForStatusUpdate(zone: Zone): ZoneInfo {
    audioCommander.requestStatus(zone)
    return zoneInfoUpdates
        .timeout(500, TimeUnit.MILLISECONDS)
        .onErrorReturn {
          LOG.error("Timed out waiting for status update on $zone")
          allZoneInfo.getValue(zone)
        }
        .filter { it.zone.zoneId == zone.zoneId }
        .blockingFirst()
  }

  private fun updateZoneFromStatus(zoneInfo: ReceivedZoneInfo) {
    val zone = zones.zone(zoneInfo.zoneId)
    val source = sources.source(zoneInfo.sourceId)

    // Convert from Russound code to something human readable.
    val volume = zoneInfo.volume
    val bass = zoneInfo.bass - 10
    val treble = zoneInfo.treble - 10
    val balance = zoneInfo.balance - 10

    val updatedInfo = ZoneInfo(zone, source, zoneInfo.power, volume, bass, treble, balance, zoneInfo.loudness)
    LOG.info("ReceivedZoneInfo from receiver: $zoneInfo, storing as $updatedInfo")

    allZoneInfo[zone] = updatedInfo
    zoneInfoUpdates.onNext(updatedInfo)
  }
}

