package holmes.ponderosa

import holmes.ponderosa.audio.AudioModule
import holmes.ponderosa.audio.DaggerAudio
import holmes.ponderosa.audio.ReceivedZoneInfo
import holmes.ponderosa.audio.RussoundCommandReceiver
import holmes.ponderosa.audio.RussoundReaderDescriptor
import holmes.ponderosa.audio.TransformerModule
import holmes.ponderosa.lights.DaggerLights
import holmes.ponderosa.lights.LightModule
import io.reactivex.subjects.PublishSubject
import org.slf4j.LoggerFactory
import spark.Spark.staticFileLocation
import spark.servlet.SparkApplication
import java.io.File
import java.io.OutputStream

private val LOG = LoggerFactory.getLogger(Ponderosa::class.java)

/** The main initializer. */
class Ponderosa(val readerDescriptor: RussoundReaderDescriptor) {
  lateinit var receivedMessageSubject: PublishSubject<ReceivedZoneInfo>
  lateinit var russoundCommandReceiver: RussoundCommandReceiver
  lateinit var outputStream: OutputStream

  val russoundReceiverThread = Thread(Runnable { russoundCommandReceiver.start() }, "russound-input-receiver")

  fun start() {
    staticFileLocation("/public/react")

    val transformerModule = TransformerModule()

    val lightsGraph = DaggerLights
        .builder()
        .lightModule(LightModule())
        .transformerModule(transformerModule)
        .build()

    val audioGraph = DaggerAudio
        .builder()
        .audioModule(AudioModule(readerDescriptor))
        .transformerModule(transformerModule)
        .build()

    outputStream = audioGraph.outputStream()
    receivedMessageSubject = audioGraph.receivedMessageSubject()
    russoundCommandReceiver = audioGraph.russoundCommandReceiver()
    russoundReceiverThread.start()

    lightsGraph.lightsRoutes().initialize()
    audioGraph.audioRoutes().initialize()
  }

  fun destroy() {
    LOG.info("Closing the output stream")
    outputStream.close()
    russoundCommandReceiver.destroy()
  }
}

/** Application for jetty deploy. */
class PonderosaSparkApplication : SparkApplication {
  val ponderosa = Ponderosa(object : RussoundReaderDescriptor {
    override val descriptor: File
      get() {
        return when {
          File("/dev/ttyUSB0").exists() -> File("/dev/ttyUSB0")
          else -> File("/dev/null")
        }
      }

    override val startMessage: Int
      get() = 0xF0

    override val endMessage: Int
      get() = 0xF7
  })

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
    }).start()
  }
}
