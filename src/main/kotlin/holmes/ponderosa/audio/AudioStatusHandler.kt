package holmes.ponderosa.audio

class AudioStatusHandler(val audioQueue: AudioQueue,
                         val commandReceiver: RussoundCommandReceiver,
                         val statusRequestTimer: AudioStatusRequestTimer) {
  fun start() {
    commandReceiver.start()
    statusRequestTimer.start()
  }

  fun stop() {
    audioQueue.destroy()
    commandReceiver.stop()
    statusRequestTimer.stop()
  }
}
