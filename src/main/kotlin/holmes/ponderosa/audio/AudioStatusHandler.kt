package holmes.ponderosa.audio

import org.holmes.russound.serial.SerialCommandReceiver

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
