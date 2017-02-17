package holmes.ponderosa.audio

import io.reactivex.subjects.BehaviorSubject

data class ZoneInfo(val zone: Zone, val source: Source, val power: Boolean, val volume: Int)

class AudioManager(val zones: Zones, val sources: Sources, private val zoneInfo: BehaviorSubject<Map<Zone, ZoneInfo>>) {
  private val allZoneInfo: MutableMap<Zone, ZoneInfo> = HashMap()

  internal fun updateZone(updatedInfo: ZoneInfo) {
    this.allZoneInfo.put(updatedInfo.zone, updatedInfo)
    this.zoneInfo.onNext(allZoneInfo)
  }
}
