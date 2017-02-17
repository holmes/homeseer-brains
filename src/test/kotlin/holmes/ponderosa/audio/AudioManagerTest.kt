package holmes.ponderosa.audio

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.mock
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream

class AudioManagerTest {
  lateinit var outputStream: ByteArrayOutputStream
  lateinit var russoundCommands: RussoundCommands
  lateinit var audioManager: AudioManager

  @Before fun setUp() {
    russoundCommands = mock<RussoundCommands> {
      on { turnOn(any()) }
          .doAnswer {
            val zone = it.arguments[0] as Zone
            "Zone${zone.zoneId}On".toByteArray()
          }

      on { turnOff(any()) }
          .doAnswer {
            val zone = it.arguments[0] as Zone
            "Zone${zone.zoneId}Off".toByteArray()
          }

      on { listen(any(), any()) }
          .doAnswer {
            val source = it.arguments[0] as Source
            val zone = it.arguments[1] as Zone
            "Zone${zone.zoneId}Source${source.sourceId}".toByteArray()
          }
    }

    outputStream = ByteArrayOutputStream()
    audioManager = AudioManager(russoundCommands, outputStream)
  }

  @Test fun powerOnTurnsOn() {
    val expected = "Zone0On".toByteArray()
    audioManager.power(Zone(0, "Place"), true)
    assertThat(outputStream.toByteArray()).isEqualTo(expected)
  }

  @Test fun powerOffTurnsOff() {
    val expected = "Zone1Off".toByteArray()
    audioManager.power(Zone(1, "Place"), false)
    assertThat(outputStream.toByteArray()).isEqualTo(expected)
  }

  @Test fun changeSourceTurnsZoneOnAndChangesSource() {
    val zone = Zone(0, "Place")
    val source = Source(0, "Source")
    val expected = "Zone${zone.zoneId}OnZone${zone.zoneId}Source${source.sourceId}".toByteArray()

    audioManager.changeSource(source, zone)
    assertThat(outputStream.toByteArray()).isEqualTo(expected)
  }
}
