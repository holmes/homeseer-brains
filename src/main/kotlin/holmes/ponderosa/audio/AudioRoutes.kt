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

    post("/api/audio/:zoneId/tripleTapOn", { request, _ ->
      val zone = request.zone() ?: return@post -1
      audioManager.power(zone, PowerChange.ON)
    })

    post("/api/audio/:zoneId/tripleTapOff", { request, _ ->
      val zone = request.zone() ?: return@post -1
      audioManager.power(zone, PowerChange.OFF)
    })

    path("/api/audio/:zoneId") {
      post("/requestStatus") { request, _ ->
        val zone = request.zone() ?: return@post -1

        audioManager.requestStatus(zone)
        return@post "Requesting requestStatus for ${zone.name}"
      }

      post("/power/:newValue") { request, _ ->
        val zone = request.zone() ?: return@post -1
        val power = request.powerOn()

        audioManager.power(zone, power)
        return@post "Turned ${power.name.toLowerCase()} ${zone.name}"
      }

      post("/volume/:newValue") { request, _ ->
        val zone = request.zone() ?: return@post -1
        val volume = request.volume()

        audioManager.volume(zone, volume)
        return@post "Turned volume ${volume.verb} in ${zone.name}"
      }

      post("/bass/:newValue") { request, _ ->
        val zone = request.zone() ?: return@post -1
        val bass = request.bass() ?: return@post -1

        audioManager.bass(zone, bass)
        return@post "Bass turned ${bass.name.toLowerCase()} in ${zone.name}"
      }

      post("/treble/:newValue") { request, _ ->
        val zone = request.zone() ?: return@post -1
        val treble = request.treble() ?: return@post -1

        audioManager.treble(zone, treble)
        return@post "Treble turned ${treble.name.toLowerCase()} in ${zone.name}"
      }

      post("/balance/:newValue") { request, _ ->
        val zone = request.zone() ?: return@post -1
        val balanceParam = request.balance() ?: return@post -1

        audioManager.balance(zone, balanceParam)
        return@post "Balance shifted ${balanceParam.name.toLowerCase()} in ${zone.name}"
      }

      post("/loudness/:newValue") { request, _ ->
        val zone = request.zone() ?: return@post -1
        val loudness = request.loudness()

        audioManager.loudness(zone, loudness)
        return@post "Loudness is now ${loudness.name.toLowerCase()} in the ${zone.name}"
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

  private fun Request.volume(): VolumeChange {
    val value = params("newValue").toLowerCase()
    return when (value) {
      "up" -> VolumeChange.Up()
      "down" -> VolumeChange.Down()
      else -> VolumeChange.Set(Math.max(0, Math.min(100, value.toInt())))
    }
  }

  private fun Request.bass(): BassLevel? {
    return when (params("newValue").toLowerCase()) {
      "down" -> BassLevel.DOWN
      "up" -> BassLevel.UP
      else -> {
        halt(400, "Bad bass param. Accepted values: [up|down]")
        null
      }
    }
  }

  private fun Request.treble(): TrebleLevel? {
    return when (params("newValue").toLowerCase()) {
      "down" -> TrebleLevel.DOWN
      "up" -> TrebleLevel.UP
      else -> {
        halt(400, "Bad treble param. Accepted values: [up|down}")
        null
      }
    }
  }

  private fun Request.balance(): Balance? {
    return when (params("newValue").toLowerCase()) {
      "left" -> Balance.LEFT
      "right" -> Balance.RIGHT
      else -> {
        halt(400, "Bad balance param:. Accepted values: [left|right]")
        null
      }
    }
  }

  private fun Request.powerOn(): PowerChange {
    val value = params("newValue")
    when (value.toLowerCase()) {
      "on", "true", "1" -> return PowerChange.ON
      else -> return PowerChange.OFF
    }
  }

  private fun Request.loudness(): Loudness {
    val value = params("newValue")
    when (value.toLowerCase()) {
      "on", "true", "1" -> return Loudness.ON
      else -> return Loudness.OFF
    }
  }
}


