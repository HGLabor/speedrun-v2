package de.hglabor.speedrun.listener

import de.hglabor.speedrun.utils.createScoreboard
import de.hglabor.speedrun.utils.updateScoreboard
import net.axay.kspigot.event.listen
import org.bukkit.event.player.PlayerJoinEvent

fun joinListener() {
    listen<PlayerJoinEvent> {
        it.player.createScoreboard()
        it.player.updateScoreboard()
    }
}
