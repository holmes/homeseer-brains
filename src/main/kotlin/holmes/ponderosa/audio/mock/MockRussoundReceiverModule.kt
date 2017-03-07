package holmes.ponderosa.audio.mock

import dagger.Component
import dagger.Module
import dagger.Provides
import holmes.ponderosa.RussoundReaderDescriptor
import holmes.ponderosa.audio.AudioQueue
import holmes.ponderosa.audio.ReceivedStatusActionHandler
import holmes.ponderosa.audio.RussoundAction
import holmes.ponderosa.audio.RussoundCommandReceiver
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.slf4j.LoggerFactory
import javax.inject.Singleton

private val LOG = LoggerFactory.getLogger(MockRussoundReceiverModule::class.java)

@Singleton
@Component(modules = arrayOf(MockRussoundReceiverModule::class))
interface MockRussoundReceiverComponent {
  fun audioQueue(): AudioQueue
  fun russoundCommandReceiver(): RussoundCommandReceiver
  fun subject(): Observable<RussoundAction>
}

@Module class MockRussoundReceiverModule(val readerDescriptor: RussoundReaderDescriptor) {
  private val name = "MATRIX"

  @Singleton @Provides fun provideAudioQueue()
      = AudioQueue(name, readerDescriptor)

  @Singleton @Provides fun provideCommands()
      = RussoundMatrixToAppCommands()

  @Singleton @Provides fun provideRussoundActionPublishSubject(): PublishSubject<RussoundAction>
      = PublishSubject.create<RussoundAction>()

  @Singleton @Provides fun provideRussoundActionObservable(subject: PublishSubject<RussoundAction>): Observable<RussoundAction>
      = subject

  @Singleton @Provides fun provideRussoundCommandReceiver(subject: PublishSubject<RussoundAction>): RussoundCommandReceiver {
    val actions = setOf(
        PowerAction(),
        ReceivedStatusActionHandler(),
        SetSourceAction(),
        VolumeDownAction(),
        VolumeUpAction(),
        VolumeSetAction()
    )

    return RussoundCommandReceiver(name, subject, actions, readerDescriptor)
  }
}

