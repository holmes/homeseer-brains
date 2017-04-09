package holmes.ponderosa.audio

import dagger.Component
import dagger.Module
import dagger.Provides
import holmes.ponderosa.RussoundReaderDescriptor
import holmes.ponderosa.transformer.JsonTransformer
import holmes.ponderosa.transformer.TransformerModule
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AudioModule::class, TransformerModule::class))
interface Audio {
  fun audioRoutes(): AudioRoutes
  fun audioStatusHandler(): AudioStatusHandler
}

@Module class AudioModule(val readerDescriptor: RussoundReaderDescriptor) {
  private val name = "PONDEROSA"

  @Singleton @Provides fun zones() = Zones()
  @Singleton @Provides fun sources() = Sources()
  @Singleton @Provides fun russoundCommands() = RussoundCommands()

  @Singleton @Provides fun receivedZoneInfoSubject() = PublishSubject.create<RussoundAction>()!!
  @Singleton @Provides fun receivedZoneInfo(subject: PublishSubject<RussoundAction>): Observable<RussoundAction> = subject

  @Singleton @Provides fun statusRequestTimer(zones: Zones, audioManager: AudioManager)
      = AudioStatusRequestTimer(zones, audioManager)

  @Singleton @Provides fun audioStatusHandler(audioQueue: AudioQueue, commandReceiver: RussoundCommandReceiver, statusRequestTimer: AudioStatusRequestTimer)
      = AudioStatusHandler(audioQueue, commandReceiver, statusRequestTimer)

  @Singleton @Provides fun russoundCommandReceiver(subject: PublishSubject<RussoundAction>): RussoundCommandReceiver {
    val actions = setOf(ReceivedStatusActionHandler())
    return RussoundCommandReceiver(name, subject, actions, readerDescriptor)
  }

  @Singleton @Provides fun audioQueue()
      = AudioQueue(name, readerDescriptor)

  @Singleton @Provides fun audioCommander(russoundCommands: RussoundCommands, audioQueue: AudioQueue)
      = AudioCommander(audioQueue, russoundCommands)

  @Singleton @Provides fun audioManager(zones: Zones, audioCommander: AudioCommander, receivedZoneInfo: Observable<RussoundAction>)
      = AudioManager(zones, audioCommander, receivedZoneInfo)

  @Singleton @Provides fun audioRoutes(zones: Zones, sources: Sources, audioManager: AudioManager, jsonTransformer: JsonTransformer)
      = AudioRoutes(zones, sources, audioManager, jsonTransformer)
}


