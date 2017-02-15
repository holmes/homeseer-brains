package holmes.ponderosa.lights

import holmes.ponderosa.audio.AudioRequest
import spark.Spark.get
import spark.Spark.halt
import spark.Spark.path
import spark.Spark.post

class AudioRoutes {
  // Getting dagger to work is just too hard. Doing it by hand for now.
  val audioRequest = AudioRequest()

  fun initialize() {
    path("/api/audio/:zoneId") {

      get("status") { request, _ ->
        val zoneId = request.params("zoneId")?.toInt()
        if (zoneId == null || zoneId !in 0..6) {
          halt(400, "Bad zoneId")
          return@get -1
        }

        audioRequest.status(zoneId)
      }

      post("/power/:newValue") { request, _ ->
        val zoneId = request.params("zoneId")?.toInt()
        if (zoneId == null || zoneId !in 0..6) {
          halt(400, "Bad zoneId")
          return@post -1
        }

        val turnOn = request.params("newValue").toBoolean()
        audioRequest.power(zoneId, turnOn)
      }

      post("/source/:sourceId") { request, _ ->
        val zoneId = request.params("zoneId")?.toInt()
        if (zoneId == null || zoneId !in 0..6) {
          halt(400, "Bad zoneId")
          return@post -1
        }

        val sourceId = request.params("sourceId")?.toInt()
        if (sourceId == null || sourceId !in 0..6) {
          halt(400, "Bad sourceId")
          return@post -1
        }

        audioRequest.change(sourceId, zoneId)
      }
    }
  }
}
