package holmes.ponderosa.audio

import com.thejholmes.russound.serial.SerialCommandReceiver

class AudioStatusHandler(val commandReceiver: SerialCommandReceiver,
                         val statusRequestTimer: AudioStatusRequestTimer) {
  fun start() {
    commandReceiver.start()
    statusRequestTimer.start()
  }

  fun stop() {
    commandReceiver.stop()
    statusRequestTimer.stop()
  }
}
