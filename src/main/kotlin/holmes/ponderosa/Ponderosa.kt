package holmes.ponderosa

import holmes.ponderosa.audio.AudioRoutes
import holmes.ponderosa.lights.LightsRoutes
import spark.servlet.SparkApplication

/** The main innitializer. */
class Ponderosa {
  fun start() {
    LightsRoutes().initialize()
    AudioRoutes().initialize()
  }
}

/** Application for jetty deploy. */
class PonderosaSparkApplication : SparkApplication {
  override fun init() {
    Ponderosa().start()
  }
}

/** Embedded application for development. */
object PonderosaEmbedded {
  @JvmStatic fun main(args: Array<String>) {
    Ponderosa().start()
  }
}
