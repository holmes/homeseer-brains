package holmes.ponderosa.audio

import io.reactivex.Observable
import org.slf4j.LoggerFactory

data class ZoneInfo(val zone: Zone, val source: Source?, val power: Boolean = false, val volume: Int = 0, val bass: Int = 0, val treble: Int = 0, val balance: Int = 0, val loudness: Boolean = false) {

  fun copy(source: Source? = this.source, power: Boolean = this.power, volume: Int = this.volume, bass: Int = this.bass, treble: Int = this.treble, balance: Int = this.balance, loudness: Boolean = this.loudness)
      = ZoneInfo(zone, source, power, volume, bass, treble, balance, loudness)
}

private const val VOLUME_STEP_LEVEL: Int = 2

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
      allZoneInfo.put(it, ZoneInfo(zone = it, source = null))
    }

    // TODO deal w/ threading. I'm ok if data is slightly out of sync for now.
    receivedZoneInfo.subscribe(this::updateZone)
  }

  fun requestStatus(zone: Zone): ZoneInfo {
    audioCommander.requestStatus(zone)
    return zoneInformation.getValue(zone)
  }

  fun power(zone: Zone, power: PowerChange): ZoneInfo {
    audioCommander.power(zone, power)
    return updateZone(true, zone) { oldZone ->
      return@updateZone oldZone.copy(power = power.isOn)
    }
  }

  fun source(zone: Zone, source: Source): ZoneInfo {
    audioCommander.changeSource(zone, source)
    return updateZone(true, zone) { oldZone ->
      return@updateZone oldZone.copy(source = source)
    }
  }

  /** Sets the initial volume when the zone is turned on. */
  fun initialVolume(zone: Zone, volume: Int) {
    // We don't pass this in the ZoneInformation. It's set once and we never care again.
    audioCommander.initialVolume(zone, volume)
  }

  fun volume(zone: Zone, volume: VolumeChange): ZoneInfo {
    audioCommander.volume(zone, volume)
    return updateZone(true, zone) { oldZone ->
      val level = when (volume) {
        is VolumeChange.Up -> (oldZone.volume.plus(VOLUME_STEP_LEVEL))
        is VolumeChange.Down -> (oldZone.volume.minus(VOLUME_STEP_LEVEL))
        is VolumeChange.Set -> volume.level
      }

      // Changing the volume automatically turns the zone on.
      return@updateZone oldZone.copy(power = true, volume = level)
    }
  }

  fun bass(zone: Zone, bass: BassLevel): ZoneInfo {
    audioCommander.bass(zone, bass)
    return updateZone(true, zone) { oldZone ->
      return@updateZone oldZone.copy(bass = bass.adjust(oldZone))
    }
  }

  fun treble(zone: Zone, treble: TrebleLevel): ZoneInfo {
    audioCommander.treble(zone, treble)
    return updateZone(true, zone) { oldZone ->
      return@updateZone oldZone.copy(treble = treble.adjust(oldZone))
    }
  }

  fun balance(zone: Zone, balance: Balance): ZoneInfo {
    audioCommander.balance(zone, balance)
    return updateZone(true, zone) { oldZone ->
      return@updateZone oldZone.copy(balance = balance.adjust(oldZone))
    }
  }

  fun loudness(zone: Zone, loudness: Loudness): ZoneInfo {
    val oldZone = allZoneInfo.getValue(zone)

    if (oldZone.loudness != loudness.isOn) {
      audioCommander.loudness(zone)
      return updateZone(true, zone) { oldZone ->
        return@updateZone oldZone.copy(loudness = !oldZone.loudness)
      }
    } else {
      return oldZone
    }
  }

  private fun updateZone(zoneInfo: ReceivedZoneInfo) {
    updateZone(false, zones.zone(zoneInfo.zoneId)) { _ ->
      val source = sources.source(zoneInfo.sourceId)

      // Convert from Russound code to something human readable.
      val volume = zoneInfo.volume
      val bass = zoneInfo.bass - 10
      val treble = zoneInfo.treble - 10
      val balance = zoneInfo.balance - 10

      val updatedInfo = ZoneInfo(zones.zone(zoneInfo.zoneId), source, zoneInfo.power, volume, bass, treble, balance, zoneInfo.loudness)
      LOG.info("Received Zone Info from Receiver: $zoneInfo, storing as $updatedInfo")
      updatedInfo
    }
  }

  /** Update the zone and request an update via AudioCommander */
  @Synchronized private fun updateZone(runUpdate: Boolean = true, zone: Zone, updateFunction: (oldZone: ZoneInfo) -> ZoneInfo): ZoneInfo {
    val oldZone = allZoneInfo.getValue(zone)
    val newZone = updateFunction(oldZone)
    allZoneInfo[zone] = newZone

    if (runUpdate) audioCommander.requestStatus(zone)

    return newZone
  }
}

private fun BassLevel.adjust(zone: ZoneInfo): Int {
  return Math.max(-10, Math.min(10, zone.bass.plus(adjustment)))
}

private fun TrebleLevel.adjust(zone: ZoneInfo): Int {
  return Math.max(-10, Math.min(10, zone.treble.plus(adjustment)))
}

private fun Balance.adjust(zone: ZoneInfo): Int {
  return Math.max(-10, Math.min(10, zone.balance.plus(adjustment)))
}

