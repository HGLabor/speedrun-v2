package de.hglabor.speedrun.listener

import de.hglabor.speedrun.config.Config
import de.hglabor.speedrun.player.UserList
import de.hglabor.speedrun.utils.col
import de.hglabor.speedrun.utils.createScoreboard
import de.hglabor.speedrun.utils.updateScoreboard
import net.axay.kspigot.event.listen
import org.bukkit.event.player.PlayerJoinEvent

fun joinListener() {
    listen<PlayerJoinEvent> {
        val players = UserList.size
        it.joinMessage = ">> ".col("green", "bold") + it.player.displayName.col("gray") +
                (" ($players/${Config.MIN_PLAYERS.get()})").col(if(players>=Config.MIN_PLAYERS.get()) "green" else "yellow")
        it.player.createScoreboard()
        it.player.updateScoreboard()
    }
}
