package holmes.ponderosa.motion

import net.pushover.client.MessagePriority.HIGH
import net.pushover.client.PushoverClient
import net.pushover.client.PushoverMessage
import org.slf4j.LoggerFactory
import spark.Route
import spark.Spark.post

private val LOG = LoggerFactory.getLogger(MotionRoutes::class.java)

class MotionRoutes(val pushoverClient: PushoverClient) {
  fun initialize() {
    post("/api/motion/:deviceId", Route { request, response ->
      val message = PushoverMessage
          .builderWithApiToken("ak2u679foxvskr9wk75t4ac852qi28")
          .setUserId("gjjnzmsqqi7irc2awr5efjgsrvmuqr")
          .setMessage("Something was moving!")
          .setPriority(HIGH)
          .build()

      pushoverClient
          .pushMessage(message)

      return@Route "message sent"
    })
  }
}
