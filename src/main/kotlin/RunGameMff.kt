import engine.core.MarioLevelGenerator
import engine.core.MarioLevelModel
import engine.core.MarioTimer
import mff.agents.common.AgentMarioGame
import java.util.*

fun main() { // mff agent and notch or krys level generator
    val game = AgentMarioGame()

    // notch level generator
    val generator: MarioLevelGenerator = levelGenerators.notch.LevelGenerator()

    // krys level generator
    val seed = Random().nextLong()
//    val generator: MarioLevelGenerator = levelGenerators.krys.LevelGenerator(seed)

    val level = generator.getGeneratedLevel(
        MarioLevelModel(150, 16),
        MarioTimer(5 * 60 * 60 * 1000)
    )
    println("GENERATED LEVEL:")
    println(level)

    // You can use this to get a level from saved file
    // val level = PlayLevel.getLevel("levels/original/lvl-1.txt")

    game.runGame(
        mff.agents.astarWindow.Agent(),
        level,
        200,
        0,
        true
    )
}
