package holmes.ponderosa.lights

import spark.ResponseTransformer
import spark.Spark.halt

/**
 * Convert a ToggleLightResult into something that Homeseer won't have a hard time parsing. Unfortunately it doesn't know JSON.
 */
class ToggleLightResultTransformer : ResponseTransformer {
  override fun render(model: Any?): String {
    if (model !is DimSchedule.ToggleLightResult) {
      halt(500, "Can only render ToggleLightResult(s)")
      return ""
    }

    return model.results
        .map { "[${it.deviceId}:${it.value}]" }
        .joinToString(",")
  }
}
