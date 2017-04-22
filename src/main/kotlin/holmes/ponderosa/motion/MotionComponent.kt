package holmes.ponderosa.motion

import dagger.Component
import dagger.Module
import dagger.Provides
import holmes.ponderosa.transformer.TransformerModule
import net.pushover.client.PushoverClient
import net.pushover.client.PushoverRestClient
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(MotionModule::class, TransformerModule::class))
interface MotionComponent {
  fun motionRoutes(): MotionRoutes
}

@Module class MotionModule {
  @Singleton @Provides fun pushOverClient(): PushoverClient {
    return PushoverRestClient()
  }

  @Singleton @Provides fun motionRoutes(pushoverClient: PushoverClient)
      = MotionRoutes(pushoverClient)
}
