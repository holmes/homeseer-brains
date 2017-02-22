package holmes.ponderosa.audio

import io.reactivex.Observable
import org.slf4j.LoggerFactory

data class ZoneInfo(val zone: Zone, val source: Source?, val power: Boolean?, val volume: Int?)

private const val VOLUME_STEP_LEVEL: Int = 2
private const val VOLUME_DEFAULT_LEVEL: Int = 30

private val LOG = LoggerFactory.getLogger(AudioManager::class.java)

/**
 * The brains of the operation. Now we have to decide - send this information to Homeseer and
 * let it be the brains, or just let this store all the info.
 */
class AudioManager(private val zones: Zones, private val sources: Sources,
                   private val audioCommander: AudioCommander, receivedZoneInfo: Observable<ReceivedZoneInfo>) {

  private val allZoneInfo: MutableMap<Zone, ZoneInfo> = HashMap()

  val zoneInformation: Map<Zone, ZoneInfo>
    get() = allZoneInfo

  init {
    zones.all.values.forEach {
      allZoneInfo.put(it, ZoneInfo(it, null, null, null))
    }

    // TODO deal w/ threading. I'm ok if data is slightly out of sync for now.
    receivedZoneInfo.subscribe(this::updateZone)
  }

  fun status(zone: Zone) {
    audioCommander.requestStatus(zone)
  }

  fun power(zone: Zone, power: PowerChange): ZoneInfo {
    audioCommander.power(zone, power)
    val oldZone = allZoneInfo[zone]
    val newZone = ZoneInfo(zone, oldZone?.source, power.isOn, oldZone?.volume)
    return updateZone(newZone)
  }

  fun source(zone: Zone, source: Source): ZoneInfo {
    audioCommander.changeSource(zone, source)
    val oldZone = allZoneInfo[zone]
    val newZone = ZoneInfo(zone, source, oldZone?.power, oldZone?.volume)
    return updateZone(newZone)
  }

  /** Sets the initial volume when the zone is turned on. */
  fun initialVolume(zone: Zone, volume: Int) {
    // We don't pass this in the ZoneInformation. It's set once and we never care again.
    audioCommander.initialVolume(zone, volume)
  }

  fun volume(zone: Zone, volume: VolumeChange): ZoneInfo {
    audioCommander.volume(zone, volume)
    val oldZone = allZoneInfo[zone]

    val level = when (volume) {
      is VolumeChange.Up -> (oldZone?.volume?.plus(VOLUME_STEP_LEVEL)) ?: VOLUME_DEFAULT_LEVEL
      is VolumeChange.Down -> (oldZone?.volume?.minus(VOLUME_STEP_LEVEL)) ?: VOLUME_DEFAULT_LEVEL
      is VolumeChange.Set -> volume.level
    }

    // Changing the volume automatically turns the zone on.
    val newZone = ZoneInfo(zone, oldZone?.source, true, level)
    return updateZone(newZone)
  }

  private fun updateZone(updatedInfo: ZoneInfo): ZoneInfo {
    this.allZoneInfo.put(updatedInfo.zone, updatedInfo)
    return updatedInfo
  }

  private fun updateZone(zoneInfo: ReceivedZoneInfo) {
    val zone = zones.zone(zoneInfo.zoneId)
    val source = sources.source(zoneInfo.sourceId)
    val updatedInfo = ZoneInfo(zone, source, zoneInfo.power, zoneInfo.volume)

    LOG.info("Received Zone Info from Receiver: $zoneInfo, storing as $updatedInfo")
    updateZone(updatedInfo)
  }
}
