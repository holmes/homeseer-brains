package holmes.ponderosa.lights

import java.time.Duration
import java.time.LocalTime

/**
 * Represents a room with various light levels defined by TimeFrame(s). TimeFrame(s) are defined by
 * their endTime(s) and are stored consecutively in the List.
 */
data class DimSchedule(val deviceId: Int, val timeFrames: List<TimeFrame>, val now: () -> LocalTime) {
  data class AutoDimResult(val dimLevel: Int, val needsReschedule: Boolean) {
    companion object Factory {
      val NO_CHANGE: AutoDimResult = AutoDimResult(-1, false)
    }
  }

  /**
   * Dim (or brighten) the lights if the calculated level is near the currentValue. We check that it's near
   * because a person in the room might have turned the lights up intentionally. We don't want to turn them
   * down if that's the case.
   */
  fun autoDim(currentValue: Int): AutoDimResult {
    if (currentValue == 0) {
      return AutoDimResult.NO_CHANGE
    }

    // If the lights aren't within this level, don't bother doing anything.
    val currentFrame = currentFrame
    if (currentValue >= currentFrame.lowLevel) {
      return AutoDimResult.NO_CHANGE
    }

    val calculatedLevel = calculateLightLevel()
    val delta = Math.abs(calculatedLevel - currentValue)
    val allowed = delta <= 3

    return if (allowed) AutoDimResult(calculatedLevel, delta > 1) else AutoDimResult.NO_CHANGE
  }

  /**
   * If the lights are off, set them to the low value.
   *
   * If they're already on, brighten or dim them depending on how bright they currently are.
   * Pick the level opposite of what the current value is currently closest to.
   */
  fun toggleLights(currentValue: Int): Int {
    if (isInLowLevel(currentValue)) {
      return currentFrame.highLevel
    } else {
      return calculateLightLevel()
    }
  }

  private fun calculateLightLevel(): Int {
    // Always use lowLevel in the morning.
    if (isInFirstFrame) {
      return timeFrames.first().lowLevel
    }

    val startValue = previousFrame.lowLevel
    val endValue = currentFrame.lowLevel

    val startTime = previousFrame.endTime
    val endTime = currentFrame.endTime
    val nowTime = now()

    val frameLength = Duration.between(startTime, endTime)
    val inset = Duration.between(startTime, nowTime)
    val ratio: Double = (inset.toMinutes().toDouble() / frameLength.toMinutes().toDouble())
    val adjustedRatio = (Math.abs(startValue - endValue) * ratio).toInt()

    var calculatedLevel: Int
    if (startValue > endValue) {
      calculatedLevel = Math.abs(adjustedRatio - startValue)
      calculatedLevel = Math.max(calculatedLevel, endValue)
    } else {
      calculatedLevel = adjustedRatio + startValue
      calculatedLevel = Math.max(calculatedLevel, startValue)
    }

    return calculatedLevel
  }

  internal fun isInLowLevel(currentValue: Int): Boolean {
    val startValue: Int
    val endValue = currentFrame.lowLevel

    if (isInFirstFrame) {
      startValue = timeFrames.last().lowLevel
    } else {
      startValue = previousFrame.lowLevel
    }

    return currentValue in startValue..endValue || currentValue in endValue..startValue
  }

  private val isInFirstFrame: Boolean
    get() = timeFrames[0].contains(now())

  internal val previousFrame: TimeFrame
    get() = timeFrames.last { !it.contains(now()) }

  internal val currentFrame: TimeFrame
    get() = timeFrames.first { it.contains(now()) }
}
