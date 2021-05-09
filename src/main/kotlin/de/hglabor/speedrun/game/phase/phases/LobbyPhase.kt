package de.hglabor.speedrun.game.phase.phases

import de.hglabor.speedrun.game.GameState
import de.hglabor.speedrun.game.phase.GamePhase
import de.hglabor.speedrun.listener.MIN_PLAYERS
import de.hglabor.speedrun.player.UserList
import org.bukkit.ChatColor

class LobbyPhase : GamePhase(0, -1, -1) {
    override fun startPreparationPhase() {}
    override fun startIngamePhase() {}
    override fun getScoreboardContent(): String = "${ChatColor.BLUE}Lobby"
    override fun getScoreboardHeading(): String = "${ChatColor.GREEN}${UserList.size}/${MIN_PLAYERS} Players"

    override fun getGameState(): GameState = GameState.Lobby
}