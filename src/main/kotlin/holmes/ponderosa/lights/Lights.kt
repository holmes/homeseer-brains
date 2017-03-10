package holmes.ponderosa.lights

import com.squareup.moshi.Moshi
import dagger.Component
import dagger.Module
import dagger.Provides
import holmes.ponderosa.transformer.TransformerModule
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(LightModule::class, TransformerModule::class))
interface Lights {
  fun lightsRoutes(): LightsRoutes
  fun twilightDataRefresher(): TwilightDataRefresher
}

@Module class LightModule(val dataLocation: File) {
  @Provides fun today(): LocalDate = LocalDate.now()
  @Provides fun now(): LocalTime = LocalTime.now()

  @Singleton @Provides fun twilightDataRefresher(twilightProvider: TwilightProvider)
      = TwilightDataRefresher(twilightProvider)

  @Singleton @Provides fun twilightProvider(moshi: Moshi, todayProvider: Provider<LocalDate>)
      = TwilightProvider(moshi, todayProvider, dataLocation)

  @Singleton @Provides fun twilight(twilightProvider: TwilightProvider)
      = Twilight({ twilightProvider.twilight() })

  @Singleton @Provides fun lightZones(twilight: Twilight): LightZones
      = LightZones(twilight)

  @Singleton @Provides fun lightToggleResultTransformer()
      = LightsTransformer()

  @Singleton @Provides fun dimCalculator(lightZones: LightZones, now: Provider<LocalTime>)
      = DimCalculator(lightZones, now)

  @Singleton @Provides fun lightsRoutes(zones: LightZones, dimCalculator: DimCalculator, lightResultTransformer: LightsTransformer)
      = LightsRoutes(zones, dimCalculator, lightResultTransformer)
}
