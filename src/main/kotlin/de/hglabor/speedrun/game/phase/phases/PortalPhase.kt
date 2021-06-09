package de.hglabor.speedrun.game.phase.phases

import de.hglabor.speedrun.config.Config
import de.hglabor.speedrun.game.GameState
import de.hglabor.speedrun.game.phase.GamePhase
import de.hglabor.speedrun.game.phase.GamePhaseManager
import de.hglabor.speedrun.player.UserList
import de.hglabor.speedrun.utils.*
import de.hglabor.speedrun.worlds.*
import de.hglabor.utils.noriskutils.SoundUtils
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.world.PortalCreateEvent
import org.bukkit.inventory.ItemStack

class PortalPhase : GamePhase(preparationDuration = 1, roundDuration = Config.PORTAL_INGAME_TIME.getInt()) {
    private val items: List<Material> = listOf(Material.DIAMOND_PICKAXE, Material.WATER_BUCKET, Material.BUCKET, Material.FLINT_AND_STEEL)
    override fun startPreparationPhase() {}
    override fun startIngamePhase() { items() }

    override fun getScoreboardHeading(): String = "Reset"
    override fun getScoreboardContent(): String = "${ChatColor.GOLD}/renew"
    override fun getGameState(): GameState = GameState.Portal

    private fun items() { UserList.players.forEach { items(it) } }
    private fun items(player: Player) {
        player.closeAndClearInv()
        player.inventory.addAll(items.stack() + ItemStack(Material.OAK_LEAVES, 64))
    }

    override fun onRenew(player: Player): Boolean {
        // When portal spawns are non-null, the clipboard is probably non-null too
        if (ingameNotFinished(player) && PORTAL_SPAWNS != null) {
            val loc = PORTAL_SPAWNS!![UserList.players.indexOf(player)]
            pastePortal(loc, requirePortalClipboard())
            player.teleport(loc)
            items(player)
            SoundUtils.playTeleportSound(player)
            return true
        }
        return false
    }

    override fun buildingAllowed(): Boolean = true

    @EventHandler
    fun onPortal(event: PortalCreateEvent) {
        if (GamePhaseManager.currentState == GameState.Portal && event.entity != null) finish(event.entity!!.uniqueId)
    }

    override fun teleportPlayers() {
        PORTAL_SPAWNS = PORTAL_SPAWNS!!.shuffled().toMutableList()
        UserList.players.forEachIndexed { index, player ->
            val loc = PORTAL_SPAWNS!![index]
            loc.x += 0.5
            loc.y = 20.0
            loc.z += 0.5
            player.teleport(loc)
            SoundUtils.playTeleportSound(player)
        }
    }
}