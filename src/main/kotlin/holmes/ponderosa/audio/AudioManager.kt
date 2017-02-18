package holmes.ponderosa.audio

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

data class ZoneInfo(val zone: Zone, val source: Source?, val power: Boolean?, val volume: Int?)

private const val VOLUME_STEP_LEVEL: Int = 2
private const val VOLUME_DEFAULT_LEVEL: Int = 30

class AudioManager(zones: Zones, val audioCommander: AudioCommander) {

  private val allZoneInfo: MutableMap<Zone, ZoneInfo> = HashMap()
  private val zoneInfoSubject: BehaviorSubject<Map<Zone, ZoneInfo>> = BehaviorSubject.create()

  val zoneInfo: Observable<Map<Zone, ZoneInfo>>
    get() = zoneInfoSubject

  init {
    zones.all.forEach {
      allZoneInfo.put(it, ZoneInfo(it, null, null, null))
    }
  }

  fun status(zone: Zone) {
    audioCommander.requestStatus(zone)
  }

  fun power(zone: Zone, power: PowerChange) {
    audioCommander.power(zone, power)
    val oldZone = allZoneInfo[zone]
    val newZone = ZoneInfo(zone, oldZone?.source, power.isOn, oldZone?.volume)
    updateZone(newZone)
  }

  fun change(zone: Zone, source: Source) {
    audioCommander.changeSource(zone, source)
    val oldZone = allZoneInfo[zone]
    val newZone = ZoneInfo(zone, source, oldZone?.power, oldZone?.volume)
    updateZone(newZone)
  }

  fun volume(zone: Zone, volume: VolumeChange) {
    audioCommander.volume(zone, volume)
    val oldZone = allZoneInfo[zone]

    val level = when (volume) {
      is VolumeChange.Up -> (oldZone?.volume?.plus(VOLUME_STEP_LEVEL)) ?: VOLUME_DEFAULT_LEVEL
      is VolumeChange.Down -> (oldZone?.volume?.minus(VOLUME_STEP_LEVEL)) ?: VOLUME_DEFAULT_LEVEL
      is VolumeChange.Set -> volume.level
    }

    val newZone = ZoneInfo(zone, oldZone?.source, oldZone?.power, level)
    updateZone(newZone)
  }

  private fun updateZone(updatedInfo: ZoneInfo) {
    this.allZoneInfo.put(updatedInfo.zone, updatedInfo)
    this.zoneInfoSubject.onNext(allZoneInfo)
  }
}
