package holmes.ponderosa.audio.mock

import holmes.ponderosa.audio.RussoundAction
import holmes.ponderosa.audio.RussoundActionHandler
import holmes.ponderosa.audio.RussoundCommands
import holmes.ponderosa.audio.ZoneInfo

class SetSourceAction : RussoundActionHandler {
  override val infoOffsets = setOf(5, 17)
  override val command = RussoundCommands.Bytes.sourceSelectBytes

  override fun createAction(input: ByteArray): RussoundAction {
    return SetSourceAction.Action(input)
  }

  internal class Action(override val input: ByteArray) : RussoundAction {
    override val zoneOffset = 5

    override fun applyTo(currentZoneInfo: ZoneInfo): ZoneInfo {
      return currentZoneInfo.copy(source = input[17].toInt())
    }

    override fun generateResponse(updatedZoneInfo: ZoneInfo): ByteArray {
      return RussoundMatrixToAppCommands.returnSource(updatedZoneInfo)
    }
  }
}

//class RequestStatusAction : RussoundActionHandler {
//  override val infoOffsets = setOf(11)
//  override val command = RussoundCommands.Bytes.statusBytes
//
//  override fun createAction(input: ByteArray): RussoundAction {
//    return RequestStatusAction.Action(input)
//  }
//
//  internal class Action(override val input: ByteArray) : RussoundAction {
//    override val zoneOffset = 11
//
//    override fun applyTo(currentZoneInfo: ZoneInfo): ZoneInfo {
//      return currentZoneInfo
//    }
//
//    override fun generateResponse(updatedZoneInfo: ZoneInfo): ByteArray {
//      return RussoundMatrixToAppCommands.returnStatus(updatedZoneInfo)
//    }
//  }
//}

class VolumeSetAction : RussoundActionHandler {
  override val infoOffsets = setOf(15, 17)
  override val command = RussoundCommands.Bytes.volumeSetBytes

  override fun createAction(input: ByteArray): RussoundAction {
    return VolumeSetAction.Action(input)
  }

  private class Action(override val input: ByteArray) : RussoundAction {
    override val zoneOffset = 17

    override fun applyTo(currentZoneInfo: ZoneInfo): ZoneInfo {
      val volume = input[15] * 2
      return currentZoneInfo.copy(power = true, volume = volume)
    }

    override fun generateResponse(updatedZoneInfo: ZoneInfo): ByteArray {
      return RussoundMatrixToAppCommands.returnVolume(updatedZoneInfo)
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

    override fun applyTo(currentZoneInfo: ZoneInfo): ZoneInfo {
      val volume = currentZoneInfo.volume + 2
      return currentZoneInfo.copy(power = true, volume = volume)
    }

    override fun generateResponse(updatedZoneInfo: ZoneInfo): ByteArray {
      return RussoundMatrixToAppCommands.returnVolume(updatedZoneInfo)
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

    override fun applyTo(currentZoneInfo: ZoneInfo): ZoneInfo {
      val volume = currentZoneInfo.volume - 2
      return currentZoneInfo.copy(power = true, volume = volume)
    }

    override fun generateResponse(updatedZoneInfo: ZoneInfo): ByteArray {
      return RussoundMatrixToAppCommands.returnVolume(updatedZoneInfo)
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

    override fun applyTo(currentZoneInfo: ZoneInfo): ZoneInfo {
      val on = input[15].toInt() == 1
      return currentZoneInfo.copy(power = on)
    }

    override fun generateResponse(updatedZoneInfo: ZoneInfo): ByteArray {
      return RussoundMatrixToAppCommands.returnPower(updatedZoneInfo)
    }
  }
}

