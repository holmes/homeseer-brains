package holmes.ponderosa

import com.google.gson.Gson
import holmes.ponderosa.audio.AudioCommander
import holmes.ponderosa.audio.AudioManager
import holmes.ponderosa.audio.AudioRoutes
import holmes.ponderosa.audio.ReceivedZoneInfo
import holmes.ponderosa.audio.RussoundCommandReceiver
import holmes.ponderosa.audio.RussoundCommands
import holmes.ponderosa.audio.RussoundReader
import holmes.ponderosa.audio.Sources
import holmes.ponderosa.audio.Zones
import holmes.ponderosa.lights.LightsRoutes
import holmes.ponderosa.util.JsonTransformer
import io.reactivex.subjects.PublishSubject
import org.slf4j.LoggerFactory
import spark.Spark.staticFileLocation
import spark.servlet.SparkApplication
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

private val LOG = LoggerFactory.getLogger(Ponderosa::class.java)

/** The main initializer. */
class Ponderosa(readerDescriptor: RussoundReader) {
  // TODO omg this needs dagger pronto.
  val receivedMessageSubject: PublishSubject<ReceivedZoneInfo> = PublishSubject.create()
  val russoundCommandReceiver = RussoundCommandReceiver(readerDescriptor, receivedMessageSubject)

  lateinit var outputStream: OutputStream
  val russoundReceiverThread = Thread(Runnable { russoundCommandReceiver.start() }, "russound-input-receiver")

  val gson = Gson()
  val jsonTransformer = JsonTransformer(gson)

  fun start() {
//    externalStaticFileLocation("src/main/webapp/public")
    staticFileLocation("/public")

    LightsRoutes().initialize()

    // Getting dagger to work is just too hard. Doing it by hand for now.
    val zones = Zones()
    val sources = Sources()

    // Not sure what we want to do w/ this for now.
    outputStream = when {
      File("/dev/ttyUSB0").exists() -> FileOutputStream("/dev/ttyUSB0")
      File("/dev/ttyUSB0").exists() -> FileOutputStream("/dev/tty.usbserial0")
      else -> ByteArrayOutputStream()
    }


    val audioCommander = AudioCommander(RussoundCommands(), outputStream)
    val audioManager = AudioManager(zones, sources, audioCommander, receivedMessageSubject)
    AudioRoutes(zones, sources, audioManager, jsonTransformer).initialize()

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
