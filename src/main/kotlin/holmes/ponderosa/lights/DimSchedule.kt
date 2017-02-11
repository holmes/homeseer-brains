package holmes.ponderosa.lights

import java.time.Duration
import java.time.LocalTime

/**
 * Represents a room with various light levels defined by TimeFrame(s). TimeFrame(s) are defined by
 * their endTime(s) and are stored consecutively in the List.
 */
data class DimSchedule(val deviceId: Int, val timeFrames: List<TimeFrame>, val now: () -> LocalTime) {
  fun autoDim(currentValue: Int): Int {
    if (currentValue == 0) {
      return -1
    }

    // If the lights aren't within this level, don't bother doing anything.
    val currentFrame = currentFrame
    if (currentValue >= currentFrame.lowLevel) {
      return -1
    }

    val calculatedLevel = calculateLightLevel()
    val allowed = Math.abs(calculatedLevel - currentValue) <= 3

    return if (allowed) calculatedLevel else -1
  }

  fun toggleLights(currentValue: Int): Int {
    return -1
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

  private val isInFirstFrame: Boolean
    get() = timeFrames.get(0).contains(now())

  internal val previousFrame: TimeFrame
    get() = timeFrames.last { !it.contains(now()) }

  internal val currentFrame: TimeFrame
    get() = timeFrames.first { it.contains(now()) }
}
