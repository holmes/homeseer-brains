package holmes.ponderosa.audio

class AudioStatusHandler(val audioCommander: AudioCommander,
                         val commandReceiver: RussoundCommandReceiver,
                         val statusRequestor: StatusRequestTimer) {
  fun start() {
    commandReceiver.start()
    statusRequestor.start()
  }

  fun stop() {
    audioCommander.destroy()
    commandReceiver.stop()
    statusRequestor.stop()
  }
}
