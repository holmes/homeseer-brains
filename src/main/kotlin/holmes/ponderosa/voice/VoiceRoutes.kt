package holmes.ponderosa.voice

import com.squareup.moshi.Moshi
import holmes.ponderosa.audio.AudioRoutes
import holmes.ponderosa.util.JsonTransformer
import holmes.ponderosa.voice.ConversationResponse.ExpectedInput
import holmes.ponderosa.voice.ConversationResponse.FinalResponse
import holmes.ponderosa.voice.ConversationResponse.SpeechResponse
import org.slf4j.LoggerFactory
import spark.Route
import spark.Spark.post

private val LOG = LoggerFactory.getLogger(AudioRoutes::class.java)

class VoiceRoutes(val moshi: Moshi, val jsonTransformer: JsonTransformer) {
  fun initialize() {
    post("/control", Route { request, response ->
      response.header("Google-Assistant-API-Version", "v1")

      val adapter = moshi.adapter(ConversationRequest::class.java)
      val conversationRequest = adapter.fromJson(request.body())
      LOG.info("Got a request: $conversationRequest")

      val initialPrompt = SpeechResponse("What is your next guess?")
      val noInputPrompt = SpeechResponse("Are you still there?")
      val inputPrompt = ExpectedInput.InputPrompt(listOf(initialPrompt), listOf(noInputPrompt))
      val expectedInput = ExpectedInput(inputPrompt, emptyList())

      val conversationResponse = ConversationResponse(null, false, FinalResponse(initialPrompt))
      LOG.info("Here's the response: $conversationResponse")

      return@Route conversationResponse
    }, jsonTransformer)
  }
}

data class ConversationRequest(val user: User, val conversation: Conversation, val inputs: List<Input>) {
  data class User(val user_id: String)
  data class Conversation(val conversation_id: String, val type: Int)
  data class Input(val intent: String, val raw_inputs: List<RawInput>) {
    data class RawInput(val input_type: Int, val query: String)
  }
}

data class ConversationResponse(val conversation_token: String?, val expect_user_response: Boolean, /*val expected_inputs: List<ExpectedInput>,*/ val final_response: FinalResponse?) {
  data class ExpectedInput(val input_prompt: InputPrompt, val possible_intents: List<ExpectedIntent>) {
    data class ExpectedIntent(val intent: String)
    data class InputPrompt(val initial_prompts: List<SpeechResponse>, val no_input_prompts: List<SpeechResponse>)
  }
  data class FinalResponse(val speech_response: SpeechResponse)
  data class SpeechResponse(val text_to_speech: String)
}
