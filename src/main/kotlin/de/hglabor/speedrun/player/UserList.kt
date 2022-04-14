package de.hglabor.speedrun.player

import de.hglabor.utils.kutils.closeAndClearInv
import de.hglabor.utils.kutils.player
import net.axay.kspigot.event.listen
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

object UserList : HashMap<UUID, SpeedRunner>() {
    fun init() {
        listen<PlayerJoinEvent> {
            this.getOrPut(it.player.uniqueId) { SpeedRunner(it.player.uniqueId) }
        }

        listen<PlayerQuitEvent> {
            this.remove(it.player.uniqueId)
        }
    }

    fun clearAndCloseAllInvs() {
        players.forEach {
            val visibilityItem = it.inventory.getItem(PlayerVisibility.SLOT)
            it.closeAndClearInv()
            it.inventory.setItem(PlayerVisibility.SLOT, visibilityItem)
        }
    }

    fun get(player: Player) = get(player.uniqueId)

    val players get(): List<Player> = keys.mapNotNull { player(it) }
}
