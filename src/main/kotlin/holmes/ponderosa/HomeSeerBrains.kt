package holmes.ponderosa

import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.application.ApplicationEnvironment
import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.routing

class HomeSeerBrains(environment: ApplicationEnvironment) : Application(environment) {
  init {
    routing {
      get("/") {
        call.respondText(ContentType.Text.Plain, "Hello, World!")
      }
    }
  }
}
