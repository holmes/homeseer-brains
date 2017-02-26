package holmes.ponderosa.audio

class AudioCommander(val audioQueue: AudioQueue, val russoundCommands: RussoundCommands) {
  fun requestStatus(zone: Zone) {
    audioQueue.sendCommand(russoundCommands.requestStatus(zone))
  }

  fun power(zone: Zone, power: PowerChange) {
    audioQueue.sendCommand(when (power) {
      PowerChange.ON -> russoundCommands.turnOn(zone)
      PowerChange.OFF -> russoundCommands.turnOff(zone)
    })
  }

  fun volume(zone: Zone, volume: VolumeChange) {
    audioQueue.sendCommand(when (volume) {
      is VolumeChange.Up -> russoundCommands.volumeUp(zone)
      is VolumeChange.Down -> russoundCommands.volumeDown(zone)
      is VolumeChange.Set -> russoundCommands.volume(zone, volume.level)
    })
  }

  fun changeSource(zone: Zone, source: Source) {
    audioQueue.sendCommand(russoundCommands.listen(zone, source))
  }

  fun bass(zone: Zone, bass: BassLevel) {
    audioQueue.sendCommand(when (bass) {
      BassLevel.UP -> russoundCommands.bassUp(zone)
      BassLevel.DOWN -> russoundCommands.bassDown(zone)
      BassLevel.FLAT -> russoundCommands.bassFlat(zone)
    })
  }

  fun treble(zone: Zone, treble: TrebleLevel) {
    audioQueue.sendCommand(when (treble) {
      TrebleLevel.UP -> russoundCommands.trebleUp(zone)
      TrebleLevel.DOWN -> russoundCommands.trebleDown(zone)
      TrebleLevel.FLAT -> russoundCommands.trebleFlat(zone)
    })
  }

  fun balance(zone: Zone, balance: Balance) {
    audioQueue.sendCommand(when (balance) {
      Balance.LEFT -> russoundCommands.balanceLeft(zone)
      Balance.RIGHT -> russoundCommands.balanceRight(zone)
      Balance.CENTER -> russoundCommands.balanceCentered(zone)
    })
  }

  fun loudness(zone: Zone) {
    audioQueue.sendCommand(russoundCommands.loudness(zone))
  }

  fun initialVolume(zone: Zone, volume: Int) {
    audioQueue.sendCommand(russoundCommands.turnOnVolume(zone, volume))
  }
}
