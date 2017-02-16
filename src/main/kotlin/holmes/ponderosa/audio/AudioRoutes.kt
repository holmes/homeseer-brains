package holmes.ponderosa.audio

import spark.Request
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
        val zone = getZone(request) ?: return@get -1
        audioRequest.status(zone)
      }

      post("/power/:newValue") { request, _ ->
        val zone = getZone(request) ?: return@post -1
        val turnOn = getPowerOn(request)

        val command = audioRequest.power(zone, turnOn)
        return@post command.map { "0x" + it.toHexString() }
      }

      post("/source/:sourceId") { request, _ ->
        val zone = getZone(request) ?: return@post -1
        val source = getSource(request) ?: return@post -1

        audioRequest.change(source, zone)
      }
    }
  }

  private fun getPowerOn(request: Request): Boolean {
    val value = request.params("newValue")
    when (value.toLowerCase()) {
      "on", "true", "1" -> return true
      else -> return false
    }
  }

  private fun getZone(request: Request): RussoundCommands.Zone? {
    return when (request.params("zoneId")?.toInt()) {
      0 -> RussoundCommands.Zone(0, "Family Room")
      1 -> RussoundCommands.Zone(1, "Kitchen")
      2 -> RussoundCommands.Zone(2, "Outside")
      3 -> RussoundCommands.Zone(3, "Master")
      4 -> RussoundCommands.Zone(4, "Nursery")
      else -> {
        halt(400, "Bad zoneId")
        return null
      }
    }
  }

  private fun getSource(request: Request): RussoundCommands.Source? {
    return when (request.params("sourceId")?.toInt()) {
      0 -> RussoundCommands.Source(0, "Family Room TV")
      1 -> RussoundCommands.Source(1, "Chromecast")
      else -> {
        halt(400, "Bad sourceId")
        return null
      }
    }
  }
}

/**
 *  Set of chars for a half-byte.
 */
private val CHARS = arrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

/**
 *  Returns the string of two characters representing the HEX value of the byte.
 */
private fun Byte.toHexString(): String {
  val i = this.toInt()
  val char2 = CHARS[i and 0x0f]
  val char1 = CHARS[i shr 4 and 0x0f]
  return "$char1$char2".toUpperCase()
}
