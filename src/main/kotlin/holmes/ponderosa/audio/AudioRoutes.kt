package holmes.ponderosa.audio

import holmes.ponderosa.util.MyHandlebarsTemplateEngine
import spark.ModelAndView
import spark.Request
import spark.Spark.get
import spark.Spark.halt
import spark.Spark.path
import spark.Spark.post

class AudioRoutes {
  // Getting dagger to work is just too hard. Doing it by hand for now.
  val zones = Zones()
  val sources = Sources()
  val audioRequest = AudioRequest()

  fun initialize() {
    get("/", { _, _ ->
      val params = HashMap<String, Any>()
      params.put("zones", zones.all)
      params.put("sources", sources.all)
      ModelAndView(params, "index.hbs")
    }, MyHandlebarsTemplateEngine())

    path("/api/audio/:zoneId") {
      get("status") { request, _ ->
        val zone = getZone(request) ?: return@get -1
        audioRequest.status(zone)
      }

      post("/power/:newValue") { request, _ ->
        val zone = getZone(request) ?: return@post -1
        val turnOn = getPowerOn(request)

        val command = audioRequest.power(zone, turnOn)
        return@post command.map { it.toHexString() }.joinToString("\\x", prefix = "\\x")
      }

      post("/volume/:newValue") { request, _ ->
        val zone = getZone(request) ?: return@post -1

        val volumeParam = request.params("newValue")
        when (volumeParam.toLowerCase()) {
          "up" -> {
            audioRequest.volumeUp(zone)
            return@post "Volume turned up in ${zone.name}"
          }
          "down" -> {
            audioRequest.volumeDown(zone)
            return@post "Volume turned down in ${zone.name}"
          }
          else -> {
            val volume = Math.min(100, volumeParam.toInt())
            audioRequest.volume(zone, volume)
            return@post "Set ${zone.name} to $volume%"
          }
        }
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

  private fun getZone(request: Request): Zone? {
    val zoneId = request.params("zoneId")?.toInt()?.minus(1)
    val zone = zoneId?.let { zones.zone(zoneId) }

    return if (zone != null) zone else {
      halt(400, "Bad zoneId")
      null
    }
  }

  private fun getSource(request: Request): Source? {
    val sourceId = request.params("sourceId")?.toInt()?.minus(1)
    val source = sourceId?.let { sources.source(it) }

    return if (source != null) source else {
      halt(400, "Bad sourceId")
      null
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
