package de.hglabor.speedrun.game.phase.phases

import de.hglabor.speedrun.PLUGIN
import de.hglabor.speedrun.game.GameState
import de.hglabor.speedrun.game.phase.GamePhase
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent

class LobbyPhase : GamePhase(0, -1, -1) {
    override fun startPreparationPhase() {}
    override fun startIngamePhase() {}
    override fun getScoreboardHeading(): String = "Leave"
    override fun getScoreboardContent(): String = "${ChatColor.RED}/hub"

    override fun getGameState(): GameState = GameState.Lobby

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) { PLUGIN.updateScoreboards() }
}