package holmes.ponderosa.audio

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.io.FileInputStream
import java.io.InputStream

private const val START_BYTE: Byte = (0xF0).toByte()
private const val END_BYTE: Byte = (0xF7).toByte()

class RussoundCommandReceiver constructor(private val receivedMessageSubject: PublishSubject<String>) {
  private enum class State {
    LOOKING_FOR_START, LOOKING_FOR_END
  }

  private var state: State
  private val inputStream: InputStream

  val receivedMessage: Observable<String>
    get() = receivedMessageSubject

  init {
    state = State.LOOKING_FOR_START
    inputStream = FileInputStream("/dev/ttyUSB0")
  }

  fun doSomething() {
    var buffer = byteArrayOf()

    while (true) {
      val read = inputStream.read().toByte()

      when (state) {
        State.LOOKING_FOR_START -> {
          if (START_BYTE == read) {
            // We got the start message, create a new message and change state.
            buffer = byteArrayOf(read)
            state = State.LOOKING_FOR_END
          } else {
            // Ignore the byte for now.
          }
        }
        State.LOOKING_FOR_END -> {
          buffer.plus(read)
          if (END_BYTE == read) {
            // We got a message. Convert it and ship it away
            receivedMessageSubject.onNext("1")
            state = State.LOOKING_FOR_START
          } else {
            // just wait until we get the next one.
          }
        }
      }
    }
  }
}
