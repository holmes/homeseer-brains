package holmes.ponderosa.util

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Template
import com.github.jknack.handlebars.cache.GuavaTemplateCache
import com.github.jknack.handlebars.io.FileTemplateLoader
import com.github.jknack.handlebars.io.TemplateSource
import com.google.common.cache.CacheBuilder
import org.eclipse.jetty.io.RuntimeIOException
import spark.ModelAndView
import spark.TemplateEngine
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Renders HTML from Route output using
 * https://github.com/jknack/handlebars.java.
 * Defaults to the 'templates' directory under the resource path.
 */
class MyHandlebarsTemplateEngine : TemplateEngine() {
  private var handlebars: Handlebars? = null

  init {
    val templateLoader = FileTemplateLoader("/work/homeseer-brains/src/main/webapp/public/templates")
//    val templateLoader = ClassPathTemplateLoader("/public/templates")
    templateLoader.suffix = null

    handlebars = Handlebars(templateLoader)

    // Set Guava cache.
    val cache = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES)
        .maximumSize(1000).build<TemplateSource, Template>()

    handlebars = handlebars!!.with(GuavaTemplateCache(cache))
  }

  override fun render(modelAndView: ModelAndView): String {
    val viewName = modelAndView.viewName
    try {
      val template = handlebars!!.compile(viewName)
      return template.apply(modelAndView.model)
    } catch (e: IOException) {
      throw RuntimeIOException(e)
    }

  }
}
