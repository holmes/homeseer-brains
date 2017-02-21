package holmes.ponderosa

import holmes.ponderosa.audio.AudioModule
import holmes.ponderosa.audio.DaggerAudio
import holmes.ponderosa.audio.ReceivedZoneInfo
import holmes.ponderosa.audio.RussoundCommandReceiver
import holmes.ponderosa.audio.RussoundReader
import holmes.ponderosa.audio.TransformerModule
import holmes.ponderosa.lights.LightsRoutes
import io.reactivex.subjects.PublishSubject
import org.slf4j.LoggerFactory
import spark.Spark.staticFileLocation
import spark.servlet.SparkApplication
import java.io.File
import java.io.OutputStream

private val LOG = LoggerFactory.getLogger(Ponderosa::class.java)

/** The main initializer. */
class Ponderosa(val readerDescriptor: RussoundReader) {
  lateinit var receivedMessageSubject: PublishSubject<ReceivedZoneInfo>
  lateinit var russoundCommandReceiver: RussoundCommandReceiver
  lateinit var outputStream: OutputStream

  val russoundReceiverThread = Thread(Runnable { russoundCommandReceiver.start() }, "russound-input-receiver")

  fun start() {
//    externalStaticFileLocation("src/main/webapp/public")
    staticFileLocation("/public")

    LightsRoutes().initialize()

    val audioGraph = DaggerAudio
        .builder()
        .audioModule(AudioModule(readerDescriptor))
        .transformerModule(TransformerModule())
        .build()

    outputStream = audioGraph.outputStream()
    receivedMessageSubject = audioGraph.receivedMessageSubject()
    russoundCommandReceiver = audioGraph.russoundCommandReceiver()

    audioGraph.audioRoutes().initialize()
    russoundReceiverThread.start()
  }

  fun destroy() {
    LOG.info("Closing the output stream")
    outputStream.close()
    russoundCommandReceiver.destroy()
  }
}

/** Application for jetty deploy. */
class PonderosaSparkApplication : SparkApplication {
  val ponderosa = Ponderosa(object : RussoundReader {
    override val descriptor: File
      get() = File("/dev/ttyUSB0")

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
    Ponderosa(object : RussoundReader {
      override val descriptor: File
        get() = File("/dev/ttys003")

      override val startMessage: Int
        get() = '0'.toInt()

      override val endMessage: Int
        get() = 'F'.toInt()
    }).start()
  }
}
