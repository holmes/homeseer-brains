package holmes.ponderosa.audio.mock

import holmes.ponderosa.audio.AudioQueue
import holmes.ponderosa.audio.ZoneInfo

class MatrixAudioCommander(val commands: RussoundMatrixToAppCommands, val audioQueue: AudioQueue) {
  fun sendStatus(zoneInfo: ZoneInfo) {
    val command = commands.returnStatus(zoneInfo)
    audioQueue.sendCommand(command)
  }
}

