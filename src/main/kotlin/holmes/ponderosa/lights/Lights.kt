package holmes.ponderosa.lights

import com.squareup.moshi.Moshi
import dagger.Component
import dagger.Module
import dagger.Provides
import holmes.ponderosa.audio.TransformerModule
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(LightModule::class, TransformerModule::class))
interface Lights {
  fun lightsRoutes(): LightsRoutes
}

@Module class LightModule {
  @Provides fun today(): LocalDate = LocalDate.now()
  @Provides fun now(): LocalTime = LocalTime.now()

  @Singleton @Provides fun twilightProvider(moshi: Moshi, todayProvider: Provider<LocalDate>)
      = TwilightProvider(moshi, todayProvider)

  @Singleton @Provides fun twilight(twilightProvider: TwilightProvider)
      = Twilight({ twilightProvider.twilight() })

  @Singleton @Provides fun lightsRequest(twilight: Twilight, now: Provider<LocalTime>)
      = LightsRequest(twilight, now)

  @Singleton @Provides fun lightsRoutes(lightsRequest: LightsRequest)
      = LightsRoutes(lightsRequest)
}