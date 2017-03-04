package holmes.ponderosa.audio

import holmes.ponderosa.util.toHexString
import io.reactivex.subjects.PublishSubject
import okio.Buffer
import org.slf4j.LoggerFactory
import kotlin.concurrent.thread

private val LOG = LoggerFactory.getLogger(RussoundCommandReceiver::class.java)

/**
 * The receiver does some weird shit. Zone 5 isn't reporting requestStatus correctly - it's missing the zone bit.
 * I have no idea if that's because there aren't any speakers connected to it - or if it's just dumb.
 *
 * Also, the USB driver doesn't flush all the bytes as they come in, so getting requestStatus will probably require an
 * extra read to ensure the requested messages make it all the way through.
 */
class RussoundCommandReceiver constructor(val readerDescriptor: RussoundReaderDescriptor, private val receivedMessageSubject: PublishSubject<ReceivedZoneInfo>) {
  private enum class State {
    LOOKING_FOR_START, LOOKING_FOR_END
  }

  private var shouldRun: Boolean = true
  private var state: State
  private val inputStream = readerDescriptor.inputStream

  init {
    state = State.LOOKING_FOR_START
  }

  fun start() {
    thread(true, true, null, "audio-status-receiver") {
      try {
        runReadLoop()
      } catch(e: Exception) {
        LOG.error("Error receiving bytes", e)
        runReadLoop()
      } finally {
        inputStream.close()
      }
    }
  }

  private fun runReadLoop() {
    var buffer = Buffer()

    while (shouldRun) {
      val read = inputStream.read()

      when (state) {
        State.LOOKING_FOR_START -> {
          if (readerDescriptor.startMessage == read) {
            buffer = Buffer()
            buffer.writeByte(read)
            state = State.LOOKING_FOR_END
          }
        }
        State.LOOKING_FOR_END -> {
          buffer = buffer.writeByte(read)
          if (readerDescriptor.endMessage == read) {
            val byteArray = buffer.readByteArray()
            val zoneInfo = ReceivedZoneInfo.from(byteArray)

            when {
              zoneInfo != null -> receivedMessageSubject.onNext(zoneInfo)
              else -> LOG.info("Ignoring message: ${byteArray.toHexString()}")
            }

            // Begin looking for the start of the next message.
            state = State.LOOKING_FOR_START
          }
        }
      }
    }
  }

  fun stop() {
    shouldRun = false
  }
}
