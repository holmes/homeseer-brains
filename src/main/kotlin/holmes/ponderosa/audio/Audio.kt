package holmes.ponderosa.audio

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.squareup.moshi.Moshi
import dagger.Component
import dagger.Module
import dagger.Provides
import holmes.ponderosa.RussoundReaderDescriptor
import holmes.ponderosa.util.JsonTransformer
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.lang.reflect.Type
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AudioModule::class, TransformerModule::class))
interface Audio {
  fun audioRoutes(): AudioRoutes
  fun audioStatusHandler(): AudioStatusHandler
}

@Module class TransformerModule {
  @Singleton @Provides fun gson(zoneInfoSerializer: ZoneInfoSerializer): Gson {
    return GsonBuilder()
        .registerTypeAdapter(ZoneInfo::class.java, zoneInfoSerializer)
        .create()
  }

  @Singleton @Provides fun moshi(): Moshi = Moshi.Builder().build()
  @Singleton @Provides fun jsonTransformer(gson: Gson) = JsonTransformer(gson)

  @Singleton @Provides fun zoneInfoSerializer(zones: Zones, sources: Sources) = ZoneInfoSerializer(zones, sources)
  class ZoneInfoSerializer(val zones: Zones, val sources: Sources) : JsonSerializer<ZoneInfo> {
    override fun serialize(src: ZoneInfo, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
      val zone = zones.zoneAt(src.zone)
      val zoneObject = JsonObject()
      zoneObject.addProperty("zoneId", zone.zoneId)
      zoneObject.addProperty("name", zone.name)

      val source = sources.sourceAt(src.source)
      val sourceObject = JsonObject()
      sourceObject.addProperty("sourceId", source.sourceId)
      sourceObject.addProperty("name", source.name)

      val rootObject = JsonObject()
      rootObject.add("zone", zoneObject)
      rootObject.add("source", sourceObject)
      rootObject.addProperty("power", src.power)
      rootObject.addProperty("volume", src.volume)
      rootObject.addProperty("bass", src.bass)
      rootObject.addProperty("treble", src.treble)
      rootObject.addProperty("balance", src.balance)
      rootObject.addProperty("loudness", src.loudness)

      return rootObject
    }
  }
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

  @Singleton @Provides fun russoundCommandReceiver(zones: Zones, sources: Sources, subject: PublishSubject<RussoundAction>): RussoundCommandReceiver {
    val actions = setOf(ReceivedStatusActionHandler(zones, sources))
    return RussoundCommandReceiver(name, subject, actions, readerDescriptor)
  }

  @Singleton @Provides fun audioQueue()
      = AudioQueue(name, readerDescriptor)

  @Singleton @Provides fun audioCommander(russoundCommands: RussoundCommands, audioQueue: AudioQueue)
      = AudioCommander(audioQueue, russoundCommands)

  @Singleton @Provides fun audioManager(zones: Zones, sources: Sources, audioCommander: AudioCommander, receivedZoneInfo: Observable<RussoundAction>)
      = AudioManager(zones, audioCommander, receivedZoneInfo)

  @Singleton @Provides fun audioRoutes(zones: Zones, sources: Sources, audioManager: AudioManager, jsonTransformer: JsonTransformer)
      = AudioRoutes(zones, sources, audioManager, jsonTransformer)
}


