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


