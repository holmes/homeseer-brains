package holmes.ponderosa.audio

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

data class ZoneInfo(val zone: Zone, val source: Source?, val power: Boolean?, val volume: Int?)

class AudioManager(zones: Zones, sources: Sources, val audioCommander: AudioCommander) {
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

  fun power(zone: Zone, power: PowerChange){
    audioCommander.power(zone, power)
  }

  fun change(zone: Zone, source: Source) {
    audioCommander.changeSource(zone, source)
  }

  fun volume(zone: Zone, volume: VolumeChange) {
    audioCommander.volume(zone, volume)
  }

  internal fun updateZone(updatedInfo: ZoneInfo) {
    this.allZoneInfo.put(updatedInfo.zone, updatedInfo)
    this.zoneInfoSubject.onNext(allZoneInfo)
  }
}
