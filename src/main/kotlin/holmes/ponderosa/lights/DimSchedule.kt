package holmes.ponderosa.lights

import java.time.Duration
import java.time.LocalTime
import javax.inject.Provider

/**
 * Represents a room with various light levels defined by TimeFrame(s). TimeFrame(s) are defined by
 * their endTime(s) and are stored consecutively in the List.
 */
data class DimSchedule(val deviceId: Int, val timeFrames: List<TimeFrame>, val now: Provider<LocalTime>) {
  /**
   * Toggling on a light can result in other lights being turned on at the same time. One example is between
   * midnight and twilight: turning on the family room light should turn on the kitchen as well.
   */
  data class ToggleLightResult(val results: Set<ToggleLightValue>) {
    data class ToggleLightValue(val deviceId: Int, val value: Int)
  }

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

    val calculatedLevel = calculateLightLevel()
    val delta = Math.abs(calculatedLevel - currentValue)
    val allowed = delta <= 3

    return if (allowed) AutoDimResult(calculatedLevel, delta > 1) else AutoDimResult.NO_CHANGE
  }

  /**
   * If the lights are off, set them to the low value.
   *
   * If they're already on, brighten or dim them depending on how bright they currently are.
   * Pick the level opposite of what the current value is closest to.
   */
  fun toggleLights(currentValue: Int): ToggleLightResult {
    val lightLevel = when {
      isInLowLevel(currentValue) -> currentFrame.highLevel
      else -> calculateLightLevel()
    }

    return ToggleLightResult(setOf(ToggleLightResult.ToggleLightValue(deviceId, lightLevel)))
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
    val nowTime = now.get()

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
    get() = timeFrames[0].contains(now.get())

  internal val previousFrame: TimeFrame
    get() = timeFrames.last { !it.contains(now.get()) }

  internal val currentFrame: TimeFrame
    get() = timeFrames.first { it.contains(now.get()) }
}
