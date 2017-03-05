package holmes.ponderosa.audio

import com.google.common.truth.Truth.assertThat
import holmes.ponderosa.RussoundReaderDescriptor
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Test

class RussoundCommandReceiverUnitTest {
  var subject = PublishSubject.create<RussoundAction>()
  lateinit var actions: Set<RussoundActionHandler>
  lateinit var descriptor: RussoundReaderDescriptor
  lateinit var receiver: RussoundCommandReceiver

  @Before fun setUp() {
//    receiver = RussoundCommandReceiver(descriptor, subject, actions)
  }

  @Test fun testSomething() {
    assertThat(true).isTrue()
  }

}
