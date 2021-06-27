package de.hglabor.speedrun.game.phase.phases

import de.hglabor.speedrun.config.Config
import de.hglabor.speedrun.game.GameState
import de.hglabor.speedrun.game.phase.GamePhase
import de.hglabor.speedrun.game.phase.GamePhaseManager
import de.hglabor.speedrun.player.UserList
import de.hglabor.speedrun.utils.*
import de.hglabor.speedrun.worlds.*
import de.hglabor.utils.noriskutils.SoundUtils
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.world.PortalCreateEvent
import org.bukkit.inventory.ItemStack
import java.util.*

class PortalPhase : GamePhase(preparationDuration = 1, roundDuration = Config.PORTAL_INGAME_TIME.getInt()) {
    private val items: List<Material> = listOf(Material.DIAMOND_PICKAXE, Material.WATER_BUCKET, Material.BUCKET, Material.FLINT_AND_STEEL)
    private var spawns: HashMap<UUID, Location> = HashMap()

    override fun startPreparationPhase() {}
    override fun startIngamePhase() { items() }

    override fun getScoreboardHeading(): String = "Reset"
    override fun getScoreboardContent(): String = "${ChatColor.GOLD}/renew"
    override fun state() = GameState.Portal

    private fun items() { UserList.players.forEach { items(it) } }
    private fun items(player: Player) {
        player.closeAndClearInv()
        player.inventory.addAll(items.stack() + ItemStack(Material.OAK_LEAVES, 64))
    }

    override fun onRenew(player: Player): Boolean {
        // When portal spawns are non-null, the clipboard is probably non-null too
        if (!ingameNotFinished(player)) return false
        spawns[player.uniqueId]?.let {
            pastePortal(it, requirePortalClipboard())
            player.teleport(it)
            items(player)
            SoundUtils.playTeleportSound(player)
            return true
        }
        return false
    }

    override fun onFall(player: Player) {
        player.teleport(spawns[player.uniqueId] ?: return)
    }

    override fun buildingAllowed(): Boolean = true

    @EventHandler
    fun onPortal(event: PortalCreateEvent) = with(event) {
        if (GamePhaseManager.currentState == GameState.Portal && entity != null) finish(entity!!.uniqueId)
    }

    override fun tpPlayers() {
        val shuffled = PORTAL_SPAWNS_LIST.shuffled()
        UserList.players.forEachIndexed { index, player ->
            spawns[player.uniqueId] = shuffled[index]
            val loc = spawns[player.uniqueId] ?: return
            loc.x += 0.5
            loc.y = 20.0
            loc.z += 0.5
            player.teleport(loc)
            SoundUtils.playTeleportSound(player)
        }
    }
}