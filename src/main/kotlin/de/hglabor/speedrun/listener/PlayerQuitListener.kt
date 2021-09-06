package de.hglabor.speedrun.listener

import de.hglabor.speedrun.config.Config
import de.hglabor.speedrun.config.PREFIX
import de.hglabor.speedrun.game.GameState
import de.hglabor.speedrun.game.phase.GamePhaseManager
import de.hglabor.speedrun.player.UserList
import de.hglabor.speedrun.utils.*
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.event.listen
import net.axay.kspigot.runnables.taskRunLater
import org.bukkit.ChatColor
import org.bukkit.event.player.PlayerQuitEvent

fun quitListener() {
    listen<PlayerQuitEvent> {
        val players = UserList.size
        it.quitMessage = "<< ".col("bold", "red") + it.player.displayName.col("gray") +
                (" ($players/${Config.MIN_PLAYERS.getInt()})").col(if(players>=Config.MIN_PLAYERS.getInt()) "green" else "yellow")
        if (GamePhaseManager.currentState == GameState.Lobby || GamePhaseManager.currentState == GameState.Win) return@listen
        if (UserList.size <= 1) {
            // Cancel game
            grayBroadcast("$PREFIX ${ChatColor.RED}Cancelled game (not enough players: ${UserList.size})")
            val restartIn = Config.CANCEL_RESTART_TIME.getInt()
            grayBroadcast("$PREFIX ${KColors.ORANGE}Restarting in ${KColors.WHITE}$restartIn ${KColors.ORANGE}second(s)")
            taskRunLater(restartIn*20L) { command("restart") }
        }
    }
}
