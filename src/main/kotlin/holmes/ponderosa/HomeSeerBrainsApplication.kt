package holmes.ponderosa

import holmes.ponderosa.lights.HomeSeerBrainsInit
import spark.servlet.SparkApplication

class HomeSeerBrainsApplication : SparkApplication {
  override fun init() {
    HomeSeerBrainsInit().start()
  }
}
