package holmes.ponderosa.motion

import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.PropertyGroup
import com.natpryce.konfig.getValue
import com.natpryce.konfig.stringType
import com.twilio.type.PhoneNumber
import dagger.Component
import dagger.Module
import dagger.Provides
import holmes.ponderosa.transformer.TransformerModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(MotionModule::class, TransformerModule::class))
interface MotionComponent {
  fun motionRoutes(): MotionRoutes
}

@Module class MotionModule {
  @Singleton @Provides fun twilioInfo(): TwilioInfo {
    return TwilioPropertyReader().createTwilioInfo()
  }

  @Singleton @Provides fun motionRoutes(twilioInfo: TwilioInfo) = MotionRoutes(twilioInfo)
}

class TwilioPropertyReader {
  object twilio : PropertyGroup() {
    val accountSid by stringType
    val authToken by stringType
    val fromPhoneNumber by stringType
    val toPhoneNumbers by stringType
  }

  fun createTwilioInfo() : TwilioInfo {
    val config = ConfigurationProperties.fromResource("twilio.properties")

    val sid = config[twilio.accountSid]
    val token = config[twilio.authToken]

    val from = config[twilio.fromPhoneNumber]
        .let { PhoneNumber(it) }

    val to = config[twilio.toPhoneNumbers]
        .splitToSequence(",")
        .map { PhoneNumber(it) }
        .toSet()

    return TwilioInfo(sid, token, from, to)
  }

}
