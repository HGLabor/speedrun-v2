package de.hglabor.speedrun.listener

import de.hglabor.speedrun.config.Config
import de.hglabor.speedrun.game.GameState
import de.hglabor.speedrun.game.phase.GamePhaseManager
import de.hglabor.speedrun.game.phase.phases.LobbyPhase
import de.hglabor.speedrun.player.UserList
import de.hglabor.speedrun.utils.col
import net.axay.kspigot.event.listen
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.player.PlayerQuitEvent

fun quitListener() {
    listen<PlayerQuitEvent> {
        val players = UserList.size
        it.quitMessage = "<< ".col("bold", "red") + it.player.displayName.col("gray")
                (" ($players/${Config.MIN_PLAYERS.getInt()})").col(if(players>=Config.MIN_PLAYERS.getInt()) "green" else "yellow")
        if (UserList.size < Config.MIN_PLAYERS.getInt() && GamePhaseManager.currentState != GameState.Lobby) {
            // Cancel game
            Bukkit.broadcastMessage("${ChatColor.RED}Cancelled game (not enough players: ${UserList.size})")
            GamePhaseManager.setPhase(LobbyPhase::class)
        }
    }
}
