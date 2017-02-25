package holmes.ponderosa.audio

class AudioStatusHandler(val audioCommander: AudioCommander,
                         val commandReceiver: RussoundCommandReceiver,
                         val statusRequestTimer: AudioStatusRequestTimer) {
  fun start() {
    commandReceiver.start()
    statusRequestTimer.start()
  }

  fun stop() {
    audioCommander.destroy()
    commandReceiver.stop()
    statusRequestTimer.stop()
  }
}
