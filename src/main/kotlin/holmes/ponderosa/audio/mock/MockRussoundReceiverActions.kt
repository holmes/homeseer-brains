package holmes.ponderosa.audio.mock

import holmes.ponderosa.audio.RussoundAction
import holmes.ponderosa.audio.RussoundActionHandler
import holmes.ponderosa.audio.RussoundCommands
import holmes.ponderosa.audio.ZoneInfo

class RequestStatusAction(val commander: MatrixAudioCommander) : RussoundActionHandler {
  override val infoOffsets = setOf(11)
  override val command = RussoundCommands.Bytes.statusBytes

  override fun createAction(input: ByteArray): RussoundAction {
    return RequestStatusAction.Action(commander, input)
  }

  private class Action(val commander: MatrixAudioCommander, override val input: ByteArray) : RussoundAction {
    override val zoneOffset: Int
      get() {
        return 11
      }

    override fun handle(currentZoneInfo: ZoneInfo): ZoneInfo {
      commander.sendStatus(currentZoneInfo)
      return currentZoneInfo
    }
  }
}

class VolumeSetAction : RussoundActionHandler {
  override val infoOffsets = setOf(15, 17)
  override val command = RussoundCommands.Bytes.volumeSetBytes

  override fun createAction(input: ByteArray): RussoundAction {
    return VolumeSetAction.Action(input)
  }

  private class Action(override val input: ByteArray) : RussoundAction {
    override val zoneOffset = 17

    override fun handle(currentZoneInfo: ZoneInfo): ZoneInfo {
      val volume = input[15] * 2
      return currentZoneInfo.copy(power = true, volume = volume)
    }
  }
}

class VolumeUpAction : RussoundActionHandler {
  override val infoOffsets = setOf(5)
  override val command = RussoundCommands.Bytes.volumeUpBytes

  override fun createAction(input: ByteArray): RussoundAction {
    return VolumeUpAction.Action(input)
  }

  private class Action(override val input: ByteArray) : RussoundAction {
    override val zoneOffset = 5

    override fun handle(currentZoneInfo: ZoneInfo): ZoneInfo {
      val volume = currentZoneInfo.volume + 2
      return currentZoneInfo.copy(power = true, volume = volume)
    }
  }
}

class VolumeDownAction : RussoundActionHandler {
  override val infoOffsets = setOf(5)
  override val command = RussoundCommands.Bytes.volumeDownBytes

  override fun createAction(input: ByteArray): RussoundAction {
    return VolumeDownAction.Action(input)
  }

  class Action(override val input: ByteArray) : RussoundAction {
    override val zoneOffset = 5

    override fun handle(currentZoneInfo: ZoneInfo): ZoneInfo {
      val volume = currentZoneInfo.volume - 2
      return currentZoneInfo.copy(power = true, volume = volume)
    }
  }
}

class PowerAction : RussoundActionHandler {
  override val infoOffsets = setOf(15, 17)
  override val command = RussoundCommands.Bytes.powerBytes

  override fun createAction(input: ByteArray): RussoundAction {
    return PowerAction.Action(input)
  }

  class Action(override val input: ByteArray) : RussoundAction {
    override val zoneOffset = 17

    override fun handle(currentZoneInfo: ZoneInfo): ZoneInfo {
      val on = input[15].toInt() == 1
      return currentZoneInfo.copy(power = on)
    }
  }
}

