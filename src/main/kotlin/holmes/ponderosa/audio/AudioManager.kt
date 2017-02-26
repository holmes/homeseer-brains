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
    val oldZone = allZoneInfo.getValue(zone)
    val newZone = oldZone.copy(power = power.isOn)
    return updateZone(newZone)
  }

  fun source(zone: Zone, source: Source): ZoneInfo {
    audioCommander.changeSource(zone, source)
    val oldZone = allZoneInfo.getValue(zone)
    val newZone = oldZone.copy(source = source)
    return updateZone(newZone)
  }

  /** Sets the initial volume when the zone is turned on. */
  fun initialVolume(zone: Zone, volume: Int) {
    // We don't pass this in the ZoneInformation. It's set once and we never care again.
    audioCommander.initialVolume(zone, volume)
  }

  fun volume(zone: Zone, volume: VolumeChange): ZoneInfo {
    audioCommander.volume(zone, volume)
    val oldZone = allZoneInfo.getValue(zone)

    val level = when (volume) {
      is VolumeChange.Up -> (oldZone.volume.plus(VOLUME_STEP_LEVEL))
      is VolumeChange.Down -> (oldZone.volume.minus(VOLUME_STEP_LEVEL))
      is VolumeChange.Set -> volume.level
    }

    // Changing the volume automatically turns the zone on.
    val newZone = oldZone.copy(power = true, volume = level)
    return updateZone(newZone)
  }

  fun bass(zone: Zone, bass: BassLevel): ZoneInfo {
    audioCommander.bass(zone, bass)
    val oldZone = allZoneInfo.getValue(zone)
    val newZone = oldZone.copy(bass = bass.adjust(oldZone))
    return updateZone(newZone)
  }

  fun treble(zone: Zone, treble: TrebleLevel): ZoneInfo {
    audioCommander.treble(zone, treble)
    val oldZone = allZoneInfo.getValue(zone)
    val newZone = oldZone.copy(treble = treble.adjust(oldZone))
    return updateZone(newZone)
  }

  fun balance(zone: Zone, balance: Balance): ZoneInfo {
    audioCommander.balance(zone, balance)
    val oldZone = allZoneInfo.getValue(zone)
    val newZone = oldZone.copy(balance = balance.adjust(oldZone))
    return updateZone(newZone)
  }

  fun loudness(zone: Zone, loudness: Loudness): ZoneInfo {
    val oldZone = allZoneInfo.getValue(zone)

    if (oldZone.loudness != loudness.isOn) {
      audioCommander.loudness(zone)
      val newZone = oldZone.copy(loudness = !oldZone.loudness)
      return updateZone(newZone)
    } else {
      return oldZone
    }
  }

  private fun updateZone(zoneInfo: ReceivedZoneInfo) {
    val zone = zones.zone(zoneInfo.zoneId)
    val source = sources.source(zoneInfo.sourceId)

    // Convert from Russound code to something human readable.
    val bass = zoneInfo.bass - 10
    val treble = zoneInfo.treble - 10
    val balance = zoneInfo.balance - 10

    val updatedInfo = ZoneInfo(zone, source, zoneInfo.power, zoneInfo.volume, bass, treble, balance, zoneInfo.loudness)

    LOG.info("Received Zone Info from Receiver: $zoneInfo, storing as $updatedInfo")
    updateZone(updatedInfo, runUpdate = false)
  }

  /** Update the zone and request an update via AudioCommander */
  private fun updateZone(updatedInfo: ZoneInfo, runUpdate: Boolean = true): ZoneInfo {
    return updatedInfo.apply {
      allZoneInfo.put(zone, updatedInfo)
      if (runUpdate) audioCommander.requestStatus(zone)
    }
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

