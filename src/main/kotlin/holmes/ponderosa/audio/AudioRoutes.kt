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
      response.type("application/json")
      LOG.info("Requested: " + request.pathInfo())
    })

    get("/api/audio/zoneInfo", Route { _, _ ->
      RootFetchInfo(
          sources = sources.all.values.sortedBy { it.sourceNumber },
          zoneInformation = audioManager.zoneInformation.values.sortedBy { it.zone }
      )
    }, jsonTransformer)

    path("/api/audio/:zoneId") {
      get("/status", Route { request, _ ->
        val zone = request.zone() ?: return@Route -1
        return@Route audioManager.zoneInformation[zone]
      }, jsonTransformer)

      post("/requestStatus", Route { request, _ ->
        val zone = request.zone() ?: return@Route -1
        return@Route audioManager.requestStatus(zone)
      }, jsonTransformer)

      post("/tripleTapOn", Route { request, _ ->
        val zone = request.zone() ?: return@Route -1
        return@Route audioManager.power(zone, PowerChange.ON)
      }, jsonTransformer)

      post("/tripleTapOff", Route { request, _ ->
        val zone = request.zone() ?: return@Route -1
        return@Route audioManager.power(zone, PowerChange.OFF)
      }, jsonTransformer)

      post("/power/:newValue", Route { request, _ ->
        val zone = request.zone() ?: return@Route -1
        val power = request.powerOn()

        return@Route audioManager.power(zone, power)
      }, jsonTransformer)

      post("/volume/:newValue", Route { request, _ ->
        val zone = request.zone() ?: return@Route -1
        val volume = request.volume()

        return@Route audioManager.volume(zone, volume)
      }, jsonTransformer)

      post("/bass/:newValue", Route { request, _ ->
        val zone = request.zone() ?: return@Route -1
        val bass = request.bass() ?: return@Route -1

        return@Route audioManager.bass(zone, bass)
      }, jsonTransformer)

      post("/treble/:newValue", Route { request, _ ->
        val zone = request.zone() ?: return@Route -1
        val treble = request.treble() ?: return@Route -1

        return@Route audioManager.treble(zone, treble)
      }, jsonTransformer)

      post("/balance/:newValue", Route { request, _ ->
        val zone = request.zone() ?: return@Route -1
        val balanceParam = request.balance() ?: return@Route -1

        return@Route audioManager.balance(zone, balanceParam)
      }, jsonTransformer)

      post("/loudness/:newValue", Route { request, _ ->
        val zone = request.zone() ?: return@Route -1
        val loudness = request.loudness()

        return@Route audioManager.loudness(zone, loudness)
      }, jsonTransformer)

      post("/source/:sourceId", Route { request, _ ->
        val zone = request.zone() ?: return@Route -1
        val source = request.source() ?: return@Route -1

        return@Route audioManager.source(zone, source)
      }, jsonTransformer)

      post("/initialVolume/:volume", Route { request, _ ->
        val zone = request.zone() ?: return@Route -1
        val volume = request.params("volume").toInt()

        return@Route audioManager.initialVolume(zone, volume)
      }, jsonTransformer)
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
      "flat" -> BassLevel.FLAT
      else -> {
        halt(400, "Bad bass param. Accepted values: [up|flat|down]")
        null
      }
    }
  }

  private fun Request.treble(): TrebleLevel? {
    return when (params("newValue").toLowerCase()) {
      "down" -> TrebleLevel.DOWN
      "up" -> TrebleLevel.UP
      "flat" -> TrebleLevel.FLAT
      else -> {
        halt(400, "Bad treble param. Accepted values: [up|flat|down}")
        null
      }
    }
  }

  private fun Request.balance(): Balance? {
    return when (params("newValue").toLowerCase()) {
      "left" -> Balance.LEFT
      "right" -> Balance.RIGHT
      "center" -> Balance.CENTER
      else -> {
        halt(400, "Bad balance param:. Accepted values: [left|center|right]")
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


