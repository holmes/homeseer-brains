package holmes.ponderosa.audio

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Test

class AudioManagerUnitTest {

  val zones = Zones()
  val sources = Sources()
  val receivedZoneInfo: PublishSubject<ReceivedZoneInfo> = PublishSubject.create()
  lateinit var audioCommander: AudioCommander
  lateinit var audioManager: AudioManager

  @Before fun setUp() {
    audioCommander = mock<AudioCommander> { }
    audioManager = AudioManager(zones, sources, audioCommander, receivedZoneInfo)
  }

  @Test fun receivedZoneInfoUpdatesZoneDoesNotAskForAnotherUpdate() {
    val zone = zones.zone(2)
    val source = sources.source(1)
    val mockData = mock<ReceivedZoneInfo> {
      on { zoneId }.doReturn(zone.zoneId)
      on { sourceId }.doReturn(source.sourceId)
      on { power }.doReturn(true)
      on { volume }.doReturn(66)
      on { bass }.doReturn(18)
      on { treble }.doReturn(10)
      on { balance }.doReturn(2)
    }

    receivedZoneInfo.onNext(mockData)
    verifyZeroInteractions(audioCommander)
  }

  @Test fun receivedZoneInfoUpdatesZone() {
    val zone = zones.zone(2)
    val source = sources.source(1)

    val mockData = mock<ReceivedZoneInfo> {
      on { zoneId }.doReturn(zone.zoneId)
      on { sourceId }.doReturn(source.sourceId)
      on { power }.doReturn(true)
      on { volume }.doReturn(66)
      on { bass }.doReturn(18)
      on { treble }.doReturn(10)
      on { balance }.doReturn(2)
    }

    val originalInfo = audioManager.zoneInformation[zone]!!
    assertThat(originalInfo.source).isNull()
    assertThat(originalInfo.power).isFalse()
    assertThat(originalInfo.volume).isEqualTo(0)

    receivedZoneInfo.onNext(mockData)

    val zoneInfo = audioManager.zoneInformation[zone]!!
    assertThat(zoneInfo.source).isEqualTo(source)
    assertThat(zoneInfo.power).isEqualTo(true)
    assertThat(zoneInfo.volume).isEqualTo(66)
    assertThat(zoneInfo.bass).isEqualTo(8)
    assertThat(zoneInfo.treble).isEqualTo(0)
    assertThat(zoneInfo.balance).isEqualTo(-8)
  }
}
