package holmes.ponderosa.audio.mock

import holmes.ponderosa.RussoundReaderDescriptor
import holmes.ponderosa.audio.ZoneInfo
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.io.OutputStream
import java.net.ServerSocket
import java.util.Random

private val LOG = LoggerFactory.getLogger(MockRussoundReceiver::class.java)
const val MATRIX_CONNECTION_PORT = 8888

class MockRussoundReceiverThread : Thread("server thread") {
  override fun run() {
    val serverSocket = ServerSocket(MATRIX_CONNECTION_PORT)
    LOG.info("Waiting for Ponderosa to connect at ${serverSocket.localPort}")

    while (true) {
      val socket = serverSocket.accept()

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

      // We're connected, let's go!
      MockRussoundReceiver(readerDescriptor).start()
    }
  }
}

class MockRussoundReceiver(val readerDescriptor: RussoundReaderDescriptor) {
  val zoneInfo: Array<ZoneInfo>

  init {
    val random = Random()
    zoneInfo = Array(6) { i ->
      ZoneInfo(i, random.nextInt(6), random.nextBoolean(), random.nextInt(100), random.nextInt(20) - 10, random.nextInt(20) - 10, random.nextInt(20) - 10, random.nextBoolean())
    }

    LOG.info("Started with zones:")
    zoneInfo.forEach {
      LOG.info("Zone: ${it.zone}, Source: ${it.source}, power: ${it.power}, volume: ${it.volume}")
    }
  }

  fun start() {
    val graph = DaggerMockRussoundReceiverComponent
        .builder()
        .mockRussoundReceiverModule(MockRussoundReceiverModule(readerDescriptor))
        .build()

    graph.audioQueue().start()
    graph.russoundCommandReceiver().start()

    graph.subject()
        .subscribe {
          val currentZoneInfo = zoneInfo[it.zone]
          val newZoneInfo = it.applyTo(currentZoneInfo)
          zoneInfo[it.zone] = newZoneInfo

          // TODO how do we want to handle sending all types of responses?
          if (it is RequestStatusAction.Action) {
            graph.audioCommander().sendStatus(newZoneInfo)
          }
        }
  }
}

