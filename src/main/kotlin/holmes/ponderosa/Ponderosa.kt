package holmes.ponderosa

import holmes.ponderosa.audio.AudioModule
import holmes.ponderosa.audio.AudioStatusHandler
import holmes.ponderosa.audio.DaggerAudio
import holmes.ponderosa.audio.RussoundReaderDescriptor
import holmes.ponderosa.audio.TransformerModule
import holmes.ponderosa.lights.DaggerLights
import holmes.ponderosa.lights.LightModule
import org.slf4j.LoggerFactory
import spark.Spark.staticFileLocation
import spark.servlet.SparkApplication
import java.io.File

private val LOG = LoggerFactory.getLogger(Ponderosa::class.java)

/** The main initializer. */
class Ponderosa(val readerDescriptor: RussoundReaderDescriptor, val twilightDataDirectory: File) {
  lateinit var audioStatusHandler: AudioStatusHandler

  fun start() {
    staticFileLocation("/public/react")

    val transformerModule = TransformerModule()

    val lightsGraph = DaggerLights
        .builder()
        .lightModule(LightModule(twilightDataDirectory))
        .transformerModule(transformerModule)
        .build()

    val audioGraph = DaggerAudio
        .builder()
        .audioModule(AudioModule(readerDescriptor))
        .transformerModule(transformerModule)
        .build()

    audioStatusHandler = audioGraph.audioStatusHandler()
    audioStatusHandler.start()

    lightsGraph.lightsRoutes().initialize()
    audioGraph.audioRoutes().initialize()
  }

  fun destroy() {
    audioStatusHandler.stop()
  }
}

/** Application for jetty deploy. */
class PonderosaSparkApplication : SparkApplication {
  val ponderosa = Ponderosa(object : RussoundReaderDescriptor {
    override val descriptor: File
      get() {
        return File("/dev")
            .walkTopDown()
            .firstOrNull { it.name.contains("ttyUSB") } ?: File("/dev/null")
      }

    override val startMessage: Int
      get() = 0xF0

    override val endMessage: Int
      get() = 0xF7
  }, File("/opt/sunrise-data/data"))

  override fun init() {
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
    Ponderosa(object : RussoundReaderDescriptor {
      override val descriptor: File
        get() = File("/dev/null")

      override val startMessage: Int
        get() = '0'.toInt()

      override val endMessage: Int
        get() = 'F'.toInt()
    }, File("/work/sunrise-data/data/")).start()
  }
}
