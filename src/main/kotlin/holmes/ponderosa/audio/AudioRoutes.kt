package holmes.ponderosa.audio

import holmes.ponderosa.util.MyHandlebarsTemplateEngine
import spark.ModelAndView
import spark.Request
import spark.Spark.get
import spark.Spark.halt
import spark.Spark.path
import spark.Spark.post
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class AudioRoutes {
  // Getting dagger to work is just too hard. Doing it by hand for now.
  val zones = Zones()
  val sources = Sources()

  // Not sure what we want to do w/ this for now.
  val outputStream: OutputStream
    get() {
      if (File("/dev/ttyUSB0").exists()) {
        return FileOutputStream("/dev/ttyUSB0")
      } else if (File("/dev/ttyUSB0").exists()) {
        return FileOutputStream("/dev/tty.usbserial0")
      } else {
        return ByteArrayOutputStream()
      }
    }

  val audioCommander = AudioCommander(RussoundCommands(), outputStream)
  val audioManager = AudioManager(zones, audioCommander)
  val templateEngine = MyHandlebarsTemplateEngine()

  fun initialize() {
    get("/", { _, _ ->
      val params = HashMap<String, Any>()
      params.put("zoneInformation", audioManager.zoneInformation.values.sortedBy { it.zone.zoneId })
      ModelAndView(params, "index.hbs")
    }, templateEngine)

    path("/api/audio/:zoneId") {
      post("/status") { request, _ ->
        val zone = request.zone() ?: return@post -1

        audioManager.status(zone)
        return@post "Requesting status for ${zone.name}"
      }

      post("/power/:newValue") { request, _ ->
        val zone = request.zone() ?: return@post -1
        val turnOn = request.powerOn()

        audioManager.power(zone, turnOn)
        val verb = if (turnOn == PowerChange.ON) "on" else "off"
        return@post "Turned $verb ${zone.name}"
      }

      post("/volume/:newValue") { request, _ ->
        val zone = request.zone() ?: return@post -1

        val volumeParam = request.params("newValue")
        when (volumeParam.toLowerCase()) {
          "up" -> {
            audioManager.volume(zone, VolumeChange.Up())
            return@post "Volume turned up in ${zone.name}"
          }
          "down" -> {
            audioManager.volume(zone, VolumeChange.Down())
            return@post "Volume turned down in ${zone.name}"
          }
          else -> {
            val volume = Math.min(100, volumeParam.toInt())
            audioManager.volume(zone, VolumeChange.Set(volume))
            return@post "Set ${zone.name} to $volume%"
          }
        }
      }

      post("/source/:sourceId") { request, _ ->
        val zone = request.zone() ?: return@post -1
        val source = request.source() ?: return@post -1

        audioManager.change(zone, source)
        return@post "${zone.name} is now listening to ${source.name}"
      }

      post("/initialVolume/:volume") { request, _ ->
        val zone = request.zone() ?: return@post -1
        val volume = request.params("volume").toInt()

        audioManager.initialVolume(zone, volume)
        return@post "${zone.name}'s turn-on volume is $volume"
      }
    }
  }

  private fun Request.zone(): Zone? {
    val zoneId = params("zoneId")?.toInt()?.minus(1)
    val zone = zoneId?.let { zones.zone(zoneId) }

    return if (zone != null) zone else {
      halt(400, "Bad zoneId")
      null
    }
  }

  private fun Request.source(): Source? {
    val sourceId = params("sourceId")?.toInt()?.minus(1)
    val source = sourceId?.let { sources.source(it) }

    return if (source != null) source else {
      halt(400, "Bad sourceId")
      null
    }
  }

  private fun Request.powerOn(): PowerChange {
    val value = params("newValue")
    when (value.toLowerCase()) {
      "on", "true", "1" -> return PowerChange.ON
      else -> return PowerChange.OFF
    }
  }
}


