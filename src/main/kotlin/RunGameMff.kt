import engine.core.MarioLevelGenerator
import engine.core.MarioLevelModel
import engine.core.MarioTimer
import mff.agents.common.AgentMarioGame
import java.util.*

fun main() { // mff agent and notch level generator
    val game = AgentMarioGame()

    // notch level generator
    val generator: MarioLevelGenerator = levelGenerators.notch.LevelGenerator()

    // notch level generator sometimes fails with an exception, try to prevent it
    var generatedSuccessfully = false
    var level = ""
    while (!generatedSuccessfully) {
        generatedSuccessfully = true
        try {
            level = generator.getGeneratedLevel(
                MarioLevelModel(150, 16),
                MarioTimer(5 * 60 * 60 * 1000)
            )
        }
        catch (e : Exception) {
            generatedSuccessfully = false
        }
    }
    println("GENERATED LEVEL:")
    println(level)

    // You can use this to get a level from saved file
    // val level = PlayLevel.getLevel("levels/original/lvl-1.txt")

    val results = game.runGame(
        mff.agents.astar.Agent(),
        level,
        200,
        0,
        true
    )

    PlayLevel.printResultsSlim(results)
}
