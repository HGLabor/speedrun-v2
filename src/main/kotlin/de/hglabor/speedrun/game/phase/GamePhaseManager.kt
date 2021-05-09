package de.hglabor.speedrun.game.phase

import de.hglabor.speedrun.PLUGIN
import de.hglabor.speedrun.game.GameState
import de.hglabor.speedrun.game.phase.phases.crafting.CraftingPhase
import de.hglabor.speedrun.game.phase.phases.LobbyPhase
import de.hglabor.speedrun.player.UserList
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

object GamePhaseManager {
    lateinit var currentPhase: GamePhase
    var currentState: GameState get() = currentPhase.getGameState(); private set(_) {}

    fun start() {
        currentPhase = LobbyPhase()
        PLUGIN.updateScoreboards()
    }

    fun nextPhase() {
        when(currentPhase.getGameState()) {
            GameState.Lobby -> setPhase(CraftingPhase::class)
            GameState.Crafting -> setPhase(LobbyPhase::class)
        }
    }

    fun setPhase(cl: KClass<out GamePhase>): GamePhase {
        // Clear/Close inventories
        UserList.clearAndCloseAllInvs()

        // Stop current phase
        currentPhase.stop()

        // Start/Return next phase
        currentPhase = cl.createInstance()
        return currentPhase
    }
}