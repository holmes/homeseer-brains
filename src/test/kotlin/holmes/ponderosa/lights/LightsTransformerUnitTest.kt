package holmes.ponderosa.lights

import com.google.common.truth.Truth.assertThat
import holmes.ponderosa.lights.DimCalculator.ToggleLightResult
import holmes.ponderosa.lights.DimCalculator.ToggleLightResult.ToggleLightValue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import spark.HaltException

class LightsTransformerUnitTest {
  lateinit var transformer: LightsTransformer

  @Before fun setUp() {
    transformer = LightsTransformer()
  }

  @Test fun failsOnNotLightToggleResult() {
    try {
      assertThat(transformer.render("ASDASD"))
      fail("Should have thrown an exception")
    } catch(e: HaltException) {
      assertThat(e.statusCode()).isEqualTo(500)
    }
  }

  @Test fun convertsSingleResult() {
    val model = ToggleLightResult(setOf(ToggleLightValue(12, 87)))
    assertThat(transformer.render(model)).isEqualTo("[12:87]")
  }

  @Test fun convertsMultipleResult() {
    val model = ToggleLightResult(setOf(
        ToggleLightValue(12, 87),
        ToggleLightValue(19, 57)
    ))

    assertThat(transformer.render(model)).isEqualTo("[12:87],[19:57]")
  }

  @Test fun convertAutoDimResult() {
    val autoDimResult = DimCalculator.AutoDimResult(22, false)
    assertThat(transformer.render(autoDimResult)).isEqualTo("22,false")
  }
}
