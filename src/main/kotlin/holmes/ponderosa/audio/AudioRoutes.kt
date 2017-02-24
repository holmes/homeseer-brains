package holmes.ponderosa.audio

import holmes.ponderosa.util.JsonTransformer
import org.slf4j.LoggerFactory
import spark.Filter
import spark.Request
import spark.Route
import spark.Spark.before
import spark.Spark.get
import spark.Spark.halt
import spark.Spark.path
import spark.Spark.post

private val LOG = LoggerFactory.getLogger(AudioRoutes::class.java)

data class RootFetchInfo(val sources: List<Source>, val zoneInformation: Collection<ZoneInfo>)

class AudioRoutes(val zones: Zones, val sources: Sources, val audioManager: AudioManager, val jsonTransformer: JsonTransformer) {
  fun initialize() {
    before(Filter { request, response ->
      response.header("Access-Control-Allow-Origin", "*")
      LOG.info("Requested: " + request.pathInfo())
    })

    get("/api/audio/zoneInfo", Route { _, response ->
      response.type("application/json")
      RootFetchInfo(
          sources = sources.all.sortedBy { it.sourceNumber },
          zoneInformation = audioManager.zoneInformation.values.sortedBy { it.zone.zoneNumber }
      )
    }, jsonTransformer)

    path("/api/audio/:zoneId") {
      post("/requestStatus") { request, _ ->
        val zone = request.zone() ?: return@post -1

        audioManager.requestStatus(zone)
        return@post "Requesting requestStatus for ${zone.name}"
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

      post("/bass/:newValue") { request, _ ->
        val zone = request.zone() ?: return@post -1

        val bassParam = request.params("newValue")
        when (bassParam.toLowerCase()) {
          "up" -> {
            audioManager.bass(zone, BassLevel.UP)
            return@post "Bass turned up in ${zone.name}"
          }
          "down" -> {
            audioManager.bass(zone, BassLevel.DOWN)
            return@post "Bass turned down in ${zone.name}"
          }
          "flat" -> {
            audioManager.bass(zone, BassLevel.FLAT)
            return@post "Bass flattened in ${zone.name}"
          }
          else -> {
            halt(400, "Unknown bass value. Accepted results: [up|flat|down] ")
          }
        }
      }

      post("/treble/:newValue") { request, _ ->
        val zone = request.zone() ?: return@post -1

        val bassParam = request.params("newValue")
        when (bassParam.toLowerCase()) {
          "up" -> {
            audioManager.treble(zone, TrebleLevel.UP)
            return@post "Treble turned up in ${zone.name}"
          }
          "down" -> {
            audioManager.treble(zone, TrebleLevel.DOWN)
            return@post "Treble turned down in ${zone.name}"
          }
          "flat" -> {
            audioManager.treble(zone, TrebleLevel.FLAT)
            return@post "Treble flattened in ${zone.name}"
          }
          else -> {
            halt(400, "Unknown treble value. Accepted results: [up|flat|down] ")
          }
        }
      }

      post("/balance/:newValue") { request, _ ->
        val zone = request.zone() ?: return@post -1

        val bassParam = request.params("newValue")
        when (bassParam.toLowerCase()) {
          "left" -> {
            audioManager.balance(zone, Balance.LEFT)
            return@post "Balance shifted left in ${zone.name}"
          }
          "right" -> {
            audioManager.balance(zone, Balance.RIGHT)
            return@post "Balance shifted right in ${zone.name}"
          }
          "center" -> {
            audioManager.balance(zone, Balance.CENTER)
            return@post "Balance centered in ${zone.name}"
          }
          else -> {
            halt(400, "Unknown treble value. Accepted results: [left|center|right] ")
          }
        }
      }

      post("/source/:sourceId") { request, _ ->
        val zone = request.zone() ?: return@post -1
        val source = request.source() ?: return@post -1

        audioManager.source(zone, source)
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
    val zoneId = params("zoneId")?.toInt()
    val zone = zoneId?.let { zones.zone(zoneId) }

    return if (zone != null) zone else {
      halt(400, "Bad zoneId")
      null
    }
  }

  private fun Request.source(): Source? {
    val sourceId = params("sourceId")?.toInt()
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


