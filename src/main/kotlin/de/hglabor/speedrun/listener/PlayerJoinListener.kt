package de.hglabor.speedrun.listener

import de.hglabor.speedrun.config.Config
import de.hglabor.speedrun.game.GameState
import de.hglabor.speedrun.game.phase.GamePhaseManager
import de.hglabor.speedrun.player.UserList
import de.hglabor.speedrun.utils.col
import de.hglabor.speedrun.utils.createScoreboard
import de.hglabor.speedrun.utils.updateScoreboard
import net.axay.kspigot.event.listen
import org.bukkit.ChatColor
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent

fun joinListener() {
    listen<PlayerJoinEvent> {
        val playerCount = UserList.size
        it.joinMessage = ">> ".col("green", "bold") + it.player.displayName.col("gray") +
                (" ($playerCount/${Config.MIN_PLAYERS.get()})").col(if (playerCount >= Config.MIN_PLAYERS.get()) "green" else "yellow")
        it.player.createScoreboard()
        it.player.updateScoreboard()
    }

    listen<PlayerLoginEvent> {
        if (GamePhaseManager.currentState == GameState.Lobby) {
            it.disallow(PlayerLoginEvent.Result.KICK_OTHER, "${ChatColor.RED}The game has already started.")
        }
    }
}
