package holmes.ponderosa.audio

class AudioStatusHandler(val audioQueue: AudioQueue,
                         val commandReceiver: RussoundCommandReceiver,
                         val statusRequestTimer: AudioStatusRequestTimer) {
  fun start() {
    audioQueue.start()
    commandReceiver.start()
    statusRequestTimer.start()
  }

  fun stop() {
    audioQueue.stop()
    commandReceiver.stop()
    statusRequestTimer.stop()
  }
}
