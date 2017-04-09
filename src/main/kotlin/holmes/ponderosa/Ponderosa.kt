package holmes.ponderosa

import holmes.ponderosa.audio.AudioModule
import holmes.ponderosa.audio.AudioStatusHandler
import holmes.ponderosa.audio.DaggerAudio
import holmes.ponderosa.transformer.TransformerModule
import holmes.ponderosa.audio.mock.MATRIX_CONNECTION_PORT
import holmes.ponderosa.audio.mock.MockRussoundReceiverThread
import holmes.ponderosa.lights.DaggerLights
import holmes.ponderosa.lights.LightModule
import holmes.ponderosa.lights.TwilightDataRefresher
import org.slf4j.LoggerFactory
import spark.Spark.staticFileLocation
import spark.servlet.SparkApplication
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

private val LOG = LoggerFactory.getLogger(Ponderosa::class.java)

interface RussoundReaderDescriptor {
  val inputStream: InputStream
  val outputStream: OutputStream

  fun destroy() {
    LOG.info("Closing input and output streams!")
    inputStream.close()
    outputStream.close()
  }
}

/** The main initializer. */
class Ponderosa(val readerDescriptor: RussoundReaderDescriptor, val twilightDataDirectory: File) {
  lateinit var audioStatusHandler: AudioStatusHandler
  lateinit var twilightDataRefresher: TwilightDataRefresher

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

    twilightDataRefresher = lightsGraph.twilightDataRefresher()
    twilightDataRefresher.start()

    audioStatusHandler = audioGraph.audioStatusHandler()
    audioStatusHandler.start()

    lightsGraph.lightsRoutes().initialize()
    audioGraph.audioRoutes().initialize()
  }

  fun destroy() {
    readerDescriptor.destroy()
    audioStatusHandler.stop()
    twilightDataRefresher.stop()
  }
}

/** Application for jetty deploy. */
class PonderosaSparkApplication : SparkApplication {
  val file = File("/dev")
      .walkTopDown()
      .firstOrNull { it.name.contains("ttyUSB") } ?: File("/dev/null")

  val ponderosa = Ponderosa(object : RussoundReaderDescriptor {
    override val inputStream: InputStream
      get() = file.inputStream()

    override val outputStream: OutputStream
      get() = file.outputStream()
  }, File("/opt/sunrise-data/data"))

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
    // Start the Mock Server.
    MockRussoundReceiverThread().start()

    // Server is started, connect the client.
    val socket = Socket("127.0.0.1", MATRIX_CONNECTION_PORT)
    LOG.info("Connected to socket on localhost:$MATRIX_CONNECTION_PORT")

    val readerDescriptor = object : RussoundReaderDescriptor {
      override val inputStream: InputStream
        get() = socket.getInputStream()

      override val outputStream: OutputStream
        get() = socket.getOutputStream()

      override fun destroy() {
        super.destroy()
        socket.close()
      }
    }

    // Then start the app.
    Ponderosa(readerDescriptor, File("/work/sunrise-data/data/")).start()
  }
}
