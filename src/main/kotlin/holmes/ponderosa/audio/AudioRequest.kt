package holmes.ponderosa.audio

class AudioRequest(val audioCommander: AudioCommander) {
  fun status(zone: Zone) {
    audioCommander.requestStatus(zone)
  }

  fun power(zone: Zone, turnOn: Boolean){
    audioCommander.power(zone, turnOn)
  }

  fun change(source: Source, zone: Zone) {
    audioCommander.changeSource(source, zone)
  }

  fun volume(zone: Zone, level: Int) {
    audioCommander.volume(zone, level)
  }

  fun volumeUp(zone: Zone) {
    audioCommander.volumeUp(zone)
  }

  fun volumeDown(zone: Zone) {
    audioCommander.volumeDown(zone)
  }
}
