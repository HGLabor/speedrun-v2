package de.hglabor.speedrun.player

import de.hglabor.speedrun.utils.closeAndClearInv
import net.axay.kspigot.event.listen
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

object UserList : HashMap<UUID, SpeedRunner>() {
    fun init() {
        Bukkit.broadcastMessage("init called");
        listen<PlayerJoinEvent> {
            this.getOrPut(it.player.uniqueId) { SpeedRunner(it.player.uniqueId) }
        }

        listen<PlayerQuitEvent> {
            this.remove(it.player.uniqueId)
        }
    }

    fun clearAndCloseAllInvs() {
        players.forEach { it.closeAndClearInv() }
    }

    fun get(player: Player) = get(player.uniqueId)

    val players get(): List<Player> = keys.mapNotNull { Bukkit.getPlayer(it) }
}
