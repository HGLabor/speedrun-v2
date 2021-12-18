package de.hglabor.speedrun.listener

import de.hglabor.speedrun.config.Config
import de.hglabor.speedrun.game.GameState
import de.hglabor.speedrun.game.phase.GamePhaseManager
import de.hglabor.speedrun.location.LOBBY_SPAWN
import de.hglabor.speedrun.player.UserList
import de.hglabor.speedrun.utils.createScoreboard
import de.hglabor.speedrun.utils.updateScoreboard
import de.hglabor.utils.kutils.*
import net.axay.kspigot.event.listen
import org.bukkit.ChatColor
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent

fun joinListener() {
    listen<PlayerJoinEvent> {
        val playerCount = UserList.size
        @Suppress("DEPRECATION")
        it.joinMessage = ">> ".col("green", "bold") + it.player.displayName.col("gray") +
                (" ($playerCount/${Config.MIN_PLAYERS.getInt()})").col(if (playerCount >= Config.MIN_PLAYERS.getInt()) "green" else "yellow")
        it.player.createScoreboard()
        it.player.updateScoreboard()

        it.player.teleport(LOBBY_SPAWN)
        it.player.survival()
        it.player.clearInv()
    }

    listen<PlayerLoginEvent> {
        if (GamePhaseManager.currentState != GameState.Lobby) {
            @Suppress("DEPRECATION")
            it.disallow(PlayerLoginEvent.Result.KICK_OTHER, "${ChatColor.RED}The game has already started.")
        }
    }
}
