package holmes.ponderosa.audio

import holmes.ponderosa.util.toHexString
import org.slf4j.LoggerFactory
import java.io.OutputStream

private val LOG = LoggerFactory.getLogger(AudioCommander::class.java)

class AudioCommander(val russoundCommands: RussoundCommands, val outputStream: OutputStream) {
  fun requestStatus(zone: Zone) {
    sendCommand(russoundCommands.requestStatus(zone))
  }

  fun power(zone: Zone, power: PowerChange) {
    sendCommand(when (power) {
      PowerChange.ON -> russoundCommands.turnOn(zone)
      PowerChange.OFF -> russoundCommands.turnOff(zone)
    })
  }

  fun volume(zone: Zone, volume: VolumeChange) {
    sendCommand(when (volume) {
      is VolumeChange.Up -> russoundCommands.volumeUp(zone)
      is VolumeChange.Down -> russoundCommands.volumeDown(zone)
      is VolumeChange.Set -> russoundCommands.volume(zone, volume.level)
    })
  }

  fun changeSource(zone: Zone, source: Source) {
    sendCommand(russoundCommands.listen(zone, source))
  }

  fun bass(zone: Zone, bass: BassLevel) {
    sendCommand(when (bass) {
      BassLevel.UP -> russoundCommands.bassUp(zone)
      BassLevel.DOWN -> russoundCommands.bassDown(zone)
      BassLevel.FLAT -> russoundCommands.bassFlat(zone)
    })
  }

  fun treble(zone: Zone, treble: TrebleLevel) {
    sendCommand(when (treble) {
      TrebleLevel.UP -> russoundCommands.trebleUp(zone)
      TrebleLevel.DOWN -> russoundCommands.trebleDown(zone)
      TrebleLevel.FLAT -> russoundCommands.trebleFlat(zone)
    })
  }

  fun balance(zone: Zone, balance: Balance) {
    sendCommand(when (balance) {
      Balance.LEFT -> russoundCommands.balanceLeft(zone)
      Balance.RIGHT -> russoundCommands.balanceRight(zone)
      Balance.CENTER -> russoundCommands.balanceCentered(zone)
    })
  }

  fun loudness(zone: Zone) {
    sendCommand(russoundCommands.loudness(zone))
  }

  fun initialVolume(zone: Zone, volume: Int) {
    sendCommand(russoundCommands.turnOnVolume(zone, volume))
  }

  fun destroy() {
    LOG.info("Closing the output stream")
    outputStream.close()
  }

  private fun sendCommand(command: ByteArray) {
    LOG.info("Sending message: ${command.toHexString()}")
    outputStream.write(command)
    outputStream.flush()
  }
}
