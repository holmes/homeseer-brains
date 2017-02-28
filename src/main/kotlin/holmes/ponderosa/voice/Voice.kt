package holmes.ponderosa.voice

import com.squareup.moshi.Moshi
import dagger.Component
import dagger.Module
import dagger.Provides
import holmes.ponderosa.audio.TransformerModule
import holmes.ponderosa.util.JsonTransformer
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(VoiceModule::class, TransformerModule::class))
interface Voice {
  fun voiceRoutes(): VoiceRoutes
}

@Module class VoiceModule {
  @Singleton @Provides fun voiceRoutes(moshi: Moshi, jsonTransformer: JsonTransformer)
      = VoiceRoutes(moshi, jsonTransformer)
}
