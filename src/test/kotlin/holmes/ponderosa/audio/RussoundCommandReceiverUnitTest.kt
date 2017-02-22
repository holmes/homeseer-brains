package holmes.ponderosa.audio

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import holmes.ponderosa.util.toHexByteArray
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Test
import java.io.File

class RussoundCommandReceiverUnitTest {
  lateinit var readerDescriptor: RussoundReaderDescriptor
  lateinit var russound: RussoundCommandReceiver
  val subject: PublishSubject<ReceivedZoneInfo> = PublishSubject.create()

  @Before fun setUp() {
    readerDescriptor = object : RussoundReaderDescriptor {
      override val descriptor: File
        get() = File("/dev/null")
      override val startMessage: Int
        get() = 0xF0
      override val endMessage: Int
        get() = 0xF7
    }
    russound = RussoundCommandReceiver(readerDescriptor, subject)
  }

  @Test fun testZone1GetStatus() {
    val expected = "F000007000000002000007000001000C00010105070A010A01000000003DF7".toHexByteArray()

    val zoneInfo = ReceivedZoneInfo.from(expected)!!
    assertWithMessage("zoneId").that(zoneInfo.zoneId).isEqualTo(0)
    assertWithMessage("power").that(zoneInfo.power).isEqualTo(true)
    assertWithMessage("sourceId").that(zoneInfo.sourceId).isEqualTo(1)
    assertWithMessage("volume").that(zoneInfo.volume).isEqualTo(10)
    assertWithMessage("bass").that(zoneInfo.bass).isEqualTo(7)
    assertWithMessage("treble").that(zoneInfo.treble).isEqualTo(10)
    assertWithMessage("loudness").that(zoneInfo.loudness).isEqualTo(true)
    assertWithMessage("balance").that(zoneInfo.balance).isEqualTo(10)
    assertWithMessage("systemOn").that(zoneInfo.systemOn).isEqualTo(true)
  }

  @Test fun testZone2GetStatus() {
    val expected = "F000007000000002000107000001000C0001010A0A0A000A010000000048F7".toHexByteArray()

    val zoneInfo = ReceivedZoneInfo.from(expected)!!
    assertWithMessage("zoneId").that(zoneInfo.zoneId).isEqualTo(1)
    assertWithMessage("power").that(zoneInfo.power).isEqualTo(true)
    assertWithMessage("sourceId").that(zoneInfo.sourceId).isEqualTo(1)
    assertWithMessage("volume").that(zoneInfo.volume).isEqualTo(20)
    assertWithMessage("bass").that(zoneInfo.bass).isEqualTo(10)
    assertWithMessage("treble").that(zoneInfo.treble).isEqualTo(10)
    assertWithMessage("loudness").that(zoneInfo.loudness).isEqualTo(false)
    assertWithMessage("balance").that(zoneInfo.balance).isEqualTo(10)
    assertWithMessage("systemOn").that(zoneInfo.systemOn).isEqualTo(true)
  }

  @Test fun testUnknownCommand() {
    val expected = "F07E00700005020100020100F13700000002000129F7".toHexByteArray()
    assertThat(ReceivedZoneInfo.from(expected)).isNull()
  }

  @Test fun testToString() {
    val input = "F000007000000002000107000001000C0001010A0A0A000A010000000048F7"
    val bytes = input.toHexByteArray()
    assertThat(ReceivedZoneInfo.from(bytes).toString()).isEqualTo(input)
  }
}
