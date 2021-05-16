package de.hglabor.speedrun.game.phase.phases

import de.hglabor.speedrun.game.GameState
import de.hglabor.speedrun.game.phase.GamePhase
import de.hglabor.speedrun.game.phase.GamePhaseManager
import de.hglabor.speedrun.player.UserList
import de.hglabor.speedrun.utils.cancel
import de.hglabor.speedrun.utils.closeAndClearInv
import de.hglabor.speedrun.worlds.Worlds
import net.axay.kspigot.extensions.geometry.add
import net.axay.kspigot.runnables.taskRunLater
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.StructureType
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerPortalEvent
import org.bukkit.inventory.ItemStack


class StrongholdPhase : GamePhase(preparationDuration = 1, roundDuration = 60) {
    override fun startPreparationPhase() {}
    override fun startIngamePhase() { items() }

    override fun getScoreboardHeading(): String = "Stuck?"
    override fun getScoreboardContent(): String = "${ChatColor.GOLD}/renew"
    override fun getGameState(): GameState = GameState.Stronghold

    private fun items() { UserList.players.forEach { items(it) } }
    private fun items(player: Player) {
        player.closeAndClearInv()
        player.inventory.addItem(ItemStack(Material.ENDER_EYE, 64))
    }

    override fun teleportPlayers() {
        val world = Worlds["stronghold"]!!
        val strongholdLoc: Location = world.locateNearestStructure(world.spawnLocation, StructureType.STRONGHOLD, 5000, false)!!
        val spawnLoc = getSpawnLoc(strongholdLoc)
        UserList.players.forEach { it.teleport(spawnLoc) }
    }

    private fun getSpawnLoc(strLoc: Location, radius: Int = 150): Location {
        var x = strLoc.x-radius
        while (x < strLoc.x+radius) {
            for (y in 25..49) {
                var z = strLoc.z-radius
                while (z < strLoc.z+radius) {
                    val block: Block = strLoc.world!!.getBlockAt(x.toInt(), y, z.toInt())
                    if (block.type == Material.COBBLESTONE_STAIRS) {
                        return block.location.clone().add(0, 1, 0)
                    }
                    z++
                }
            }
            x++
        }
        return strLoc
    }

    @EventHandler
    fun onPortal(event: PlayerPortalEvent) {
        if (GamePhaseManager.currentState == GameState.Stronghold && ingameNotFinished(event.player)) {
            event.cancel()
            finish(event.player.uniqueId)
            taskRunLater(5L) { event.player.teleport(event.from) } // Tp back because its broken
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onInteract(event: PlayerInteractEvent) {
        if (event.item?.type == Material.ENDER_EYE && event.clickedBlock?.type != Material.END_PORTAL_FRAME) event.cancel()
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (event.blockAgainst.type == Material.END_PORTAL_FRAME) {
            event.isCancelled = false
            event.setBuild(true)
        }
    }
}