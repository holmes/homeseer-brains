package holmes.ponderosa.audio

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.mock
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream

class AudioCommanderUnitTest {
  lateinit var outputStream: ByteArrayOutputStream
  lateinit var russoundCommands: RussoundCommands
  lateinit var audioCommander: AudioCommander

  val zone0 = Zone(0, 0, 0, "Place")
  val zone1 = Zone(0, 1, 1, "Place")
  val source0 = Source(0, 0, 0, "TV")

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
            val zone = it.arguments[0] as Zone
            val source = it.arguments[1] as Source
            "Zone${zone.zoneId}Source${source.sourceId}".toByteArray()
          }

      on { volume(any(), any()) }
          .doAnswer {
            val zone = it.arguments[0] as Zone
            val level = it.arguments[1] as Int
            "Zone${zone.zoneId}Volume$level".toByteArray()
          }

      on { volumeUp(any()) }
          .doAnswer {
            val zone = it.arguments[0] as Zone
            "Zone${zone.zoneId}VolumeUp".toByteArray()
          }

      on { volumeDown(any()) }
          .doAnswer {
            val zone = it.arguments[0] as Zone
            "Zone${zone.zoneId}VolumeDown".toByteArray()
          }
    }

    outputStream = ByteArrayOutputStream()
    audioCommander = AudioCommander(russoundCommands, outputStream)
  }

  @Test fun powerOnTurnsOn() {
    val expected = "Zone0On".toByteArray()
    audioCommander.power(zone0, PowerChange.ON)
    assertThat(outputStream.toByteArray()).isEqualTo(expected)
  }

  @Test fun powerOffTurnsOff() {
    val expected = "Zone1Off".toByteArray()
    audioCommander.power(zone1, PowerChange.OFF)
    assertThat(outputStream.toByteArray()).isEqualTo(expected)
  }

  @Test fun volumeUp() {
    val expected = "Zone1VolumeUp".toByteArray()
    audioCommander.volume(zone1, VolumeChange.Up())
    assertThat(outputStream.toByteArray()).isEqualTo(expected)
  }

  @Test fun volumeDown() {
    val expected = "Zone1VolumeDown".toByteArray()
    audioCommander.volume(zone1, VolumeChange.Down())
    assertThat(outputStream.toByteArray()).isEqualTo(expected)
  }
  @Test fun volumeSet() {
    val expected = "Zone1Volume22".toByteArray()
    audioCommander.volume(zone1, VolumeChange.Set(22))
    assertThat(outputStream.toByteArray()).isEqualTo(expected)
  }

  @Test fun changeSourceChangesSource() {
    val zone = zone0
    val source = source0
    val expected = "Zone${zone.zoneId}Source${source.sourceId}".toByteArray()

    audioCommander.changeSource(zone, source)
    assertThat(outputStream.toByteArray()).isEqualTo(expected)
  }
}
