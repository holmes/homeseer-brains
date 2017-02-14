package holmes.ponderosa

import holmes.ponderosa.lights.HomeSeerBrainsInit

object HomeSeerBrainsEmbedded {
  @JvmStatic fun main(args: Array<String>) {
    HomeSeerBrainsInit().start()
  }
}
