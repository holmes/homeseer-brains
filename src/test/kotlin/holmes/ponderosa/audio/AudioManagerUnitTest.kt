package holmes.ponderosa.audio

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
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

  @Test fun powerChangeUpdatesPower() {
    val oldZone = audioManager.zoneInformation[zones.zone(0)]!!
    assertThat(oldZone.power).isNull()

    var newZoneInfo = audioManager.power(oldZone.zone, PowerChange.OFF)
    assertThat(newZoneInfo.power).isFalse()

    newZoneInfo = audioManager.power(oldZone.zone, PowerChange.ON)
    assertThat(newZoneInfo.power).isTrue()
  }

  @Test fun sourceChangeUpdatesSource() {
    val oldZone = audioManager.zoneInformation[zones.zone(1)]!!
    assertThat(oldZone.source).isNull()

    val selectedSource = sources.source(0)
    val newZoneInfo = audioManager.source(oldZone.zone, selectedSource)
    assertThat(newZoneInfo.source).isEqualTo(selectedSource)
  }

  @Test fun updateVolumeSetsToDefaultLevelWhenUnknown() {
    val oldZone = audioManager.zoneInformation[zones.zone(1)]!!
    assertThat(oldZone.volume).isNull()

    val (_, _, _, volume) = audioManager.volume(oldZone.zone, VolumeChange.Up())
    assertThat(volume).isEqualTo(30)
  }

  @Test fun updateVolumeAlwaysTurnsOnZoneWhenNull() {
    val oldZone = audioManager.zoneInformation[zones.zone(1)]!!
    assertThat(oldZone.power).isNull()

    val (_, _, power) = audioManager.volume(oldZone.zone, VolumeChange.Up())
    assertThat(power).isTrue()
  }

  @Test fun updateVolumeAlwaysTurnsOnZoneWhenOff() {
    val oldZone = audioManager.power(zones.zone(1), PowerChange.OFF)
    assertThat(oldZone.power).isFalse()

    val (_, _, power) = audioManager.volume(oldZone.zone, VolumeChange.Up())
    assertThat(power).isTrue()
  }

  @Test fun receivedZoneInfoUpdatesZone() {
    val zone = zones.zone(2)
    val source = sources.source(1)

    val mockData = mock<ReceivedZoneInfo> {
      on { zoneId }.doReturn(zone.zoneId)
      on { sourceId }.doReturn(source.sourceId)
      on { power }.doReturn(true)
      on { volume }.doReturn(66)
    }

    val originalInfo = audioManager.zoneInformation[zone]!!
    assertThat(originalInfo.source).isNull()
    assertThat(originalInfo.power).isNull()
    assertThat(originalInfo.volume).isNull()

    receivedZoneInfo.onNext(mockData)

    val zoneInfo = audioManager.zoneInformation[zone]!!
    assertThat(zoneInfo.source).isEqualTo(source)
    assertThat(zoneInfo.power).isEqualTo(true)
    assertThat(zoneInfo.volume).isEqualTo(66)
  }
}
