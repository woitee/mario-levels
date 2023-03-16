package adapters

import engine.core.MarioAgent
import engine.core.MarioGame
import engine.core.MarioResult
import mff.agents.common.AgentMarioGame
import mff.agents.common.IMarioAgentMFF
import mff.forwardmodel.slim.core.MarioResultSlim

/**
 * Serves as an adapter facilitating both the original model by Khalifa and new model by Sosvald.
 */
class UniversalMarioGame {
    private val oldGame: MarioGame by lazy { MarioGame() }
    private val game: AgentMarioGame by lazy { AgentMarioGame() }

    fun runGame(agent: MarioAgent, level: String, timer: Int, marioState: Int, visuals: Boolean = false): MarioResult
        = oldGame.runGame(agent, level, timer, marioState, visuals)

    fun runGame(agent: IMarioAgentMFF, level: String, timer: Int, marioState: Int, visuals: Boolean = false): MarioResultSlim
        = game.runGame(agent, level, timer, marioState, visuals)
}