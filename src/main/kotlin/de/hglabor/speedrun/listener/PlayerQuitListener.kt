package de.hglabor.speedrun.listener

import de.hglabor.speedrun.game.GameState
import de.hglabor.speedrun.game.phase.GamePhaseManager
import de.hglabor.speedrun.game.phase.phases.LobbyPhase
import de.hglabor.speedrun.player.UserList
import net.axay.kspigot.event.listen
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.player.PlayerQuitEvent

const val MIN_PLAYERS = 1
fun quitListener() {
    listen<PlayerQuitEvent> {
        if (UserList.size < MIN_PLAYERS && GamePhaseManager.currentState != GameState.Lobby) {
            // Cancel game
            Bukkit.broadcastMessage("${ChatColor.RED}Cancelled game (not enough players: ${UserList.size})")
            GamePhaseManager.setPhase(LobbyPhase::class)
        }
    }
}
