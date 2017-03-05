package holmes.ponderosa.audio.mock

//class RussoundCommandInterpreterTest {
//  lateinit var commands: RussoundCommands
//  lateinit var interpreter: RussoundCommandInterpreter
//
//  private val zone1 = Zone(0, 0, 1, "1")
//  private val zone2 = Zone(0, 1, 2, "2")
//
//  @Before
//  fun setUp() {
//    commands = RussoundCommands()
//    interpreter = RussoundCommandInterpreter.Factory.create()
//  }
//
//  @Test fun dontFindAnything() {
//    val sentCommand = byteArrayOf(12, 34, 56)
//    assertThat(interpreter.findAction(sentCommand)).isNull()
//  }
//
//  @Test fun canWeFindSomethingForZone1() {
//    val sentCommand = commands.volumeUp(zone1)
//    assertThat(interpreter.findAction(sentCommand)).isNotNull()
//  }
//
//  @Test fun canWeFindSomethingForZone2() {
//    val sentCommand = commands.volume(zone2, 88)
//    assertThat(interpreter.findAction(sentCommand)).isNotNull()
//  }
//}
