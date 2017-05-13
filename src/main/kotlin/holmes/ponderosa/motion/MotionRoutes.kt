package holmes.ponderosa.motion

import com.twilio.Twilio
import com.twilio.rest.api.v2010.account.Message
import com.twilio.rest.api.v2010.account.MessageCreator
import com.twilio.type.PhoneNumber
import org.slf4j.LoggerFactory
import spark.Route
import spark.Spark.post

private val LOG = LoggerFactory.getLogger(MotionRoutes::class.java)

data class TwilioInfo(val accountSid: String, val authToken: String, val fromPhoneNumber: PhoneNumber, val toPhoneNumbers: Set<PhoneNumber>)

class MotionRoutes(val twilioInfo: TwilioInfo) {
  fun initialize() {
    val accountSid = twilioInfo.accountSid
    val authToken = twilioInfo.authToken

    Twilio.init(accountSid, authToken)
    LOG.info("Twilio Information: $twilioInfo")

    post("/api/motion/:deviceId", Route { _, _ ->
      twilioInfo.toPhoneNumbers
          .map { createMessage(it) }
          .map { it.create() }
          .forEach { LOG.debug("Sent message: ${it.sid}") }

      return@Route "message sent"
    })
  }

  private fun createMessage(it: PhoneNumber): MessageCreator {
    return Message.creator(
        it,
        twilioInfo.fromPhoneNumber,
        "There's someone at the door"
    )
  }
}
