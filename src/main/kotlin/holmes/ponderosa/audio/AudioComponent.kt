package holmes.ponderosa.audio

import dagger.Component
import dagger.Module
import dagger.Provides
import holmes.ponderosa.RussoundReaderDescriptor
import holmes.ponderosa.transformer.JsonTransformer
import holmes.ponderosa.transformer.TransformerModule
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.holmes.russound.Russound
import org.holmes.russound.RussoundAction
import org.holmes.russound.RussoundCommander
import org.holmes.russound.RussoundTranslator
import org.holmes.russound.RussoundZoneInfoListener
import org.holmes.russound.ZoneInfo
import org.holmes.russound.serial.SerialCommandReceiver
import org.holmes.russound.serial.SerialCommandSender
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AudioModule::class, TransformerModule::class))
interface Audio {
  fun audioRoutes(): AudioRoutes
  fun audioStatusHandler(): AudioStatusHandler
}

@Module class AudioModule(val readerDescriptor: RussoundReaderDescriptor) {
  private val logger = LoggerFactory.getLogger(AudioModule::class.java)
  private val name = "PONDEROSA"

  @Singleton @Provides fun zones() = Zones()
  @Singleton @Provides fun sources() = Sources()

  @Singleton @Provides fun receivedZoneInfoSubject() = PublishSubject.create<ZoneInfo>()!!
  @Singleton @Provides fun receivedZoneInfo(subject: PublishSubject<ZoneInfo>): Observable<ZoneInfo> = subject

  @Singleton @Provides fun statusRequestTimer(zones: Zones, audioManager: AudioManager)
      = AudioStatusRequestTimer(zones, audioManager)

  @Singleton @Provides fun audioStatusHandler(commandReceiver: SerialCommandReceiver, statusRequestTimer: AudioStatusRequestTimer)
      = AudioStatusHandler(commandReceiver, statusRequestTimer)

  @Singleton @Provides fun russoundCommandReceiver(translator: RussoundTranslator): SerialCommandReceiver
      = SerialCommandReceiver(translator, readerDescriptor.inputStream)

  @Singleton @Provides fun russoundTranslator(subject: PublishSubject<ZoneInfo>): RussoundTranslator {
    return Russound.receiver(name, object: RussoundZoneInfoListener {
      override fun onNext(action: RussoundAction) {
        logger.info("Received action from matrix: ${action.description}")
      }

      override fun updated(zoneInfo: ZoneInfo) {
        logger.info("Received ZoneInfo for Zone ${zoneInfo.zone}")
        subject.onNext(zoneInfo)
      }
    })
  }

  @Singleton @Provides fun serialCommandSender() : SerialCommandSender
      = SerialCommandSender(readerDescriptor.outputStream)

  @Singleton @Provides fun russoundCommander(sender: SerialCommandSender) : RussoundCommander
      = Russound.sender(name, sender)

  @Singleton @Provides fun audioManager(zones: Zones, audioCommander: RussoundCommander, receivedZoneInfo: Observable<ZoneInfo>)
      = AudioManager(zones, audioCommander, receivedZoneInfo)

  @Singleton @Provides fun audioRoutes(zones: Zones, sources: Sources, audioManager: AudioManager, jsonTransformer: JsonTransformer)
      = AudioRoutes(zones, sources, audioManager, jsonTransformer)
}


