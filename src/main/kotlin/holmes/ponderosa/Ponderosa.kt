package holmes.ponderosa

import holmes.ponderosa.lights.DaggerLights
import holmes.ponderosa.lights.LightModule
import holmes.ponderosa.lights.TwilightDataRefresher
import holmes.ponderosa.motion.DaggerMotionComponent
import holmes.ponderosa.motion.MotionModule
import org.slf4j.LoggerFactory
import spark.Spark.staticFileLocation
import spark.servlet.SparkApplication
import java.io.File

private val LOG = LoggerFactory.getLogger(Ponderosa::class.java)

/** The main initializer. */
class Ponderosa(val twilightDataDirectory: File) {
  lateinit var twilightDataRefresher: TwilightDataRefresher

  fun start() {
    staticFileLocation("/public/react")

    val lightsGraph = DaggerLights
        .builder()
        .lightModule(LightModule(twilightDataDirectory))
        .build()

    val motionGraph = DaggerMotionComponent
        .builder()
        .motionModule(MotionModule())
        .build()

    twilightDataRefresher = lightsGraph.twilightDataRefresher()
    twilightDataRefresher.start()

    lightsGraph.lightsRoutes().initialize()
    motionGraph.motionRoutes().initialize()
  }

  fun destroy() {
    twilightDataRefresher.stop()
  }
}

/** Application for jetty deploy. */
class PonderosaSparkApplication : SparkApplication {
  val file = File("/dev")
      .walkTopDown()
      .firstOrNull { it.name.contains("ttyUSB") } ?: File("/dev/null")

  val ponderosa = Ponderosa(File("/opt/sunrise-data/data"))

  override fun init() {
    LOG.info("Using ${file.absolutePath} to communicate w/ the matrix.")
    ponderosa.start()
  }

  override fun destroy() {
    LOG.info("Destroying the PonderosaSparkApplication")
    ponderosa.destroy()
  }
}

/** Embedded application for development. */
object PonderosaEmbedded {
  @JvmStatic fun main(args: Array<String>) {

    // Then start the app.
    Ponderosa(File("/work/sunrise-data/data/")).start()
  }
}
