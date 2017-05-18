package holmes.ponderosa.transformer

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import holmes.ponderosa.audio.Sources
import holmes.ponderosa.audio.Zones
import org.holmes.russound.ZoneInfo
import java.lang.reflect.Type
import javax.inject.Singleton

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
