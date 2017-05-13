package holmes.ponderosa.motion

import com.twilio.type.PhoneNumber
import dagger.Component
import dagger.Module
import dagger.Provides
import holmes.ponderosa.transformer.TransformerModule
import java.util.ResourceBundle
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(MotionModule::class, TransformerModule::class))
interface MotionComponent {
  fun motionRoutes(): MotionRoutes
}

@Module class MotionModule {
  @Singleton @Provides fun twilioInfo(): TwilioInfo {
    return TwilioPropertyReader().fromClasspath()
  }

  @Singleton @Provides fun motionRoutes(twilioInfo: TwilioInfo) = MotionRoutes(twilioInfo)
}

class TwilioPropertyReader {
  fun fromClasspath(): TwilioInfo {
    val bundle = ResourceBundle.getBundle("twilio")

    val sid = bundle.getString("twilio.accountSid")
    val token = bundle.getString("twilio.authToken")

    val from = bundle.getString("twilio.fromPhoneNumber")
        .let { PhoneNumber(it) }

    val to = bundle.getString("twilio.toPhoneNumbers")
        .splitToSequence(",")
        .map { PhoneNumber(it) }
        .toSet()

    return TwilioInfo(sid, token, from, to)
  }
}
