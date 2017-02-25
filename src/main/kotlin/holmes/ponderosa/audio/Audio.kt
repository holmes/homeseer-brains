package holmes.ponderosa.audio

import com.google.gson.Gson
import com.squareup.moshi.Moshi
import dagger.Component
import dagger.Module
import dagger.Provides
import holmes.ponderosa.util.JsonTransformer
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Qualifier
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AudioModule::class, TransformerModule::class))
interface Audio {
  fun audioRoutes(): AudioRoutes
  fun audioStatusHandler(): AudioStatusHandler
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RussoundCAA66

@Module class TransformerModule {
  @Singleton @Provides fun gson() = Gson()
  @Singleton @Provides fun moshi(): Moshi = Moshi.Builder().build()

  @Singleton @Provides fun jsonTransformer(gson: Gson) = JsonTransformer(gson)
}

@Module class AudioModule(val readerDescriptor: RussoundReaderDescriptor) {
  @Singleton @Provides fun zones() = Zones()
  @Singleton @Provides fun sources() = Sources()
  @Singleton @Provides fun russoundCommands() = RussoundCommands()

  @Singleton @Provides fun receivedZoneInfoSubject() = PublishSubject.create<ReceivedZoneInfo>()!!
  @Singleton @Provides fun receivedZoneInfo(subject: PublishSubject<ReceivedZoneInfo>): Observable<ReceivedZoneInfo> = subject

  @RussoundCAA66 @Singleton @Provides fun outputStream(): OutputStream {
    return FileOutputStream(readerDescriptor.descriptor)
  }

  @Singleton @Provides fun statusRequestTimer(zones: Zones, audioManager: AudioManager)
      = AudioStatusRequestTimer(zones, audioManager)

  @Singleton @Provides fun audioStatusHandler(audioCommander: AudioCommander, commandReceiver: RussoundCommandReceiver, statusRequestor: AudioStatusRequestTimer)
      = AudioStatusHandler(audioCommander, commandReceiver, statusRequestor)

  @Singleton @Provides fun russoundCommandReceiver(subject: PublishSubject<ReceivedZoneInfo>)
      = RussoundCommandReceiver(readerDescriptor, subject)

  @Singleton @Provides fun audioCommander(russoundCommands: RussoundCommands, @RussoundCAA66 outputStream: OutputStream)
      = AudioCommander(russoundCommands, outputStream)

  @Singleton @Provides fun audioManager(zones: Zones, sources: Sources, audioCommander: AudioCommander, receivedZoneInfo: Observable<ReceivedZoneInfo>)
      = AudioManager(zones, sources, audioCommander, receivedZoneInfo)

  @Singleton @Provides fun audioRoutes(zones: Zones, sources: Sources, audioManager: AudioManager, jsonTransformer: JsonTransformer)
      = AudioRoutes(zones, sources, audioManager, jsonTransformer)
}


