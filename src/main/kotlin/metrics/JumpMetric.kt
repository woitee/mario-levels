package metrics

import adapters.UniversalMarioGame
import engine.helper.EventType

class JumpMetric: AbstractMetric() {
    override val name = "jumps"

    override fun getValue(level: String): String {
        val game = UniversalMarioGame()
        val results = game.runGame(
            mff.agents.astar.Agent(),
            level,
            200,
            0
        )

        val jumpCount = results.gameEvents.filter { it.eventType == EventType.JUMP.value }.count()
        return jumpCount.toString()
    }
}