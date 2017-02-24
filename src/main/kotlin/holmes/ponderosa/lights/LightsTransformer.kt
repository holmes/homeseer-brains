package holmes.ponderosa.lights

import spark.ResponseTransformer
import spark.Spark.halt

/**
 * Convert results from LightsRoutes into something that Homeseer won't have a hard time parsing. Unfortunately it doesn't know JSON.
 */
class LightsTransformer : ResponseTransformer {
  override fun render(model: Any?): String {
    return when (model) {
      is DimCalculator.AutoDimResult -> renderAutoDim(model)
      is DimCalculator.ToggleLightResult -> renderToggle(model)
      else -> {
        halt(500, "Cannot render ${model?.toString()}")
        ""
      }
    }
  }

  private fun renderAutoDim(model: DimCalculator.AutoDimResult)
      = "${model.dimLevel},${model.needsReschedule}"

  private fun renderToggle(model: DimCalculator.ToggleLightResult): String {
    return model.results
        .map { "[${it.deviceId}:${it.value}]" }
        .joinToString(",")
  }
}
