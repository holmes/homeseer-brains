package holmes.ponderosa.audio

import holmes.ponderosa.util.toHexString
import org.eclipse.jetty.util.ArrayQueue
import org.slf4j.LoggerFactory
import java.io.OutputStream
import java.util.concurrent.Executors

private val LOG = LoggerFactory.getLogger(AudioQueue::class.java)

class AudioQueue(val outputStream: OutputStream) {
  private val queue = ArrayQueue<ByteArray>()
  private val executor = Executors.newSingleThreadExecutor({
    Thread(it, "russound-command-writer")
  })

  private var actionPending = false
  private var destroyed = false

  fun destroy() {
    destroyed = true
  }

  @Synchronized fun sendCommand(command: ByteArray) {
    if (destroyed) return

    if (queue.isEmpty() && !actionPending) {
      performAction(command)
    } else {
      queue.add(command)
    }
  }

  private fun performAction(command: ByteArray) {
    executor.submit {
      LOG.info("Sending message: ${command.toHexString()}")
      outputStream.write(command)
      outputStream.flush()

      Thread.sleep(150)
      onActionCompleted()
    }
  }

  @Synchronized private fun onActionCompleted() {
    actionPending = false

    val action = queue.poll()
    if (action != null) {
      performAction(action)
    }
  }
}
