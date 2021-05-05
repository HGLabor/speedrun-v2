package de.hglabor.speedrun.player

import net.axay.kspigot.event.listen
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

object UserList : HashMap<UUID, User>() {
    init {
        listen<PlayerJoinEvent> {
            this.getOrPut(it.player.uniqueId) { User(it.player.uniqueId) }
        }

        listen<PlayerQuitEvent> {
            this.remove(it.player.uniqueId)
        }
    }

    val players get(): List<Player> = keys.mapNotNull { Bukkit.getPlayer(it) }
}
