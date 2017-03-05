package holmes.ponderosa.audio.mock

import dagger.Component
import dagger.Module
import dagger.Provides
import holmes.ponderosa.RussoundReaderDescriptor
import holmes.ponderosa.audio.AudioQueue
import holmes.ponderosa.audio.RussoundAction
import holmes.ponderosa.audio.RussoundCommandReceiver
import io.reactivex.subjects.PublishSubject
import org.slf4j.LoggerFactory
import javax.inject.Singleton

private val LOG = LoggerFactory.getLogger(MockRussoundReceiverModule::class.java)

@Singleton
@Component(modules = arrayOf(MockRussoundReceiverModule::class))
interface MockRussoundReceiverComponent {
  fun audioQueue(): AudioQueue
  fun audioCommander(): MatrixAudioCommander
  fun russoundCommandReceiver(): RussoundCommandReceiver
  fun subject(): PublishSubject<RussoundAction>
}

@Module class MockRussoundReceiverModule(val readerDescriptor: RussoundReaderDescriptor) {
  private val name = "MATRIX"

  @Singleton @Provides fun provideAudioQueue()
      = AudioQueue(name, readerDescriptor)

  @Singleton @Provides fun provideCommands()
      = RussoundMatrixToAppCommands()

  @Singleton @Provides fun provideMatrixAudioCommander(commands: RussoundMatrixToAppCommands, audioQueue: AudioQueue)
      = MatrixAudioCommander(commands, audioQueue)

  @Singleton @Provides fun provideRussoundActionPublishSubject(): PublishSubject<RussoundAction>
      = PublishSubject.create<RussoundAction>()

  @Singleton @Provides fun provideRussoundCommandReceiver(commander: MatrixAudioCommander, subject: PublishSubject<RussoundAction>): RussoundCommandReceiver {
    val actions = setOf(
        RequestStatusAction(),
        VolumeDownAction(),
        VolumeUpAction(),
        VolumeSetAction(),
        PowerAction())

    return RussoundCommandReceiver(name, subject, actions, readerDescriptor)
  }
}

