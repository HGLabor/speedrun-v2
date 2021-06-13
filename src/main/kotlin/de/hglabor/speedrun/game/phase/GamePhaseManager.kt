package de.hglabor.speedrun.game.phase

import de.hglabor.speedrun.PLUGIN
import de.hglabor.speedrun.game.GameState
import de.hglabor.speedrun.game.phase.phases.*
import de.hglabor.speedrun.game.phase.phases.crafting.CraftingPhase
import de.hglabor.speedrun.player.UserList
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

object GamePhaseManager {
    lateinit var currentPhase: GamePhase
    val currentState by lazy { currentPhase.state() }

    fun start() {
        currentPhase = LobbyPhase()
        PLUGIN.updateScoreboards()
    }

    fun nextPhase() {
        currentPhase = when (currentPhase.state()) {
            GameState.Lobby -> setPhase(CraftingPhase::class)
            GameState.Crafting -> setPhase(PortalPhase::class)
            GameState.Portal -> setPhase(StrongholdPhase::class)
            GameState.Stronghold -> setPhase(CrystalPhase::class)
            GameState.Crystal -> setPhase(WinPhase::class)
            GameState.Win -> setPhase(LobbyPhase::class)
        }
    }

    fun setPhase(cl: KClass<out GamePhase>): GamePhase {
        // Clear/Close inventories
        UserList.clearAndCloseAllInvs()

        // Stop current phase
        currentPhase.stop()

        // Start/Return next phase
        currentPhase = cl.createInstance()

        PLUGIN.updateScoreboards()

        return currentPhase
    }
}