package holmes.ponderosa.util

import com.google.gson.Gson
import spark.ResponseTransformer


/**
 * I've given up figuring out how to get Moshi to just convert anything I pass to it.
 * Gson is just so damn easy.
 */
class JsonTransformer(val gson: Gson) : ResponseTransformer {
  override fun render(model: Any?): String {
    return when (model) {
      null -> ""
      else -> gson.toJson(model)
    }
  }
}
