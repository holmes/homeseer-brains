package holmes.ponderosa.audio

import org.slf4j.LoggerFactory
import java.io.InputStream
import java.io.OutputStream

private val LOG = LoggerFactory.getLogger(RussoundReaderDescriptor::class.java)

interface RussoundReaderDescriptor {
  val inputStream: InputStream
  val outputStream: OutputStream
  val startMessage: Int
  val endMessage: Int

  fun destroy() {
    LOG.info("Closing input and output streams!")
    inputStream.close()
    outputStream.close()
  }
}
