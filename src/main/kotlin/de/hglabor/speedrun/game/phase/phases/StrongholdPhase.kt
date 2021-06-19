package de.hglabor.speedrun.game.phase.phases

import de.hglabor.speedrun.config.Config
import de.hglabor.speedrun.game.GameState
import de.hglabor.speedrun.game.phase.GamePhase
import de.hglabor.speedrun.game.phase.GamePhaseManager
import de.hglabor.speedrun.player.UserList
import de.hglabor.speedrun.utils.cancel
import de.hglabor.speedrun.utils.closeAndClearInv
import net.axay.kspigot.extensions.geometry.add
import net.axay.kspigot.runnables.taskRunLater
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.block.data.Openable
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerPortalEvent
import org.bukkit.inventory.ItemStack


class StrongholdPhase : GamePhase(preparationDuration = 1, roundDuration = Config.STRONGHOLD_INGAME_TIME.getInt()) {
    private lateinit var spawnLoc: Location

    override fun startPreparationPhase() {}
    override fun startIngamePhase() { items() }

    override fun getScoreboardHeading(): String = "Stuck?"
    override fun getScoreboardContent(): String = "${ChatColor.GOLD}/renew"
    override fun state() = GameState.Stronghold

    private fun items() { UserList.players.forEach { items(it) } }
    private fun items(player: Player) {
        player.closeAndClearInv()
        player.inventory.addItem(ItemStack(Material.ENDER_EYE, 64))
    }

    override fun startRoundTP() = with(GameState.Stronghold.world) {
        val strongholdLoc: Location = locateNearestStructure(spawnLocation, StructureType.STRONGHOLD, 5000, false)!!
        spawnLoc = getSpawnLoc(strongholdLoc)
        UserList.players.forEach { it.teleport(spawnLoc) }
    }

    override fun onRenew(player: Player): Boolean {
        if (this::spawnLoc.isInitialized) {
            player.teleport(spawnLoc)
            return true
        }
        return false
    }

    private fun getSpawnLoc(strLoc: Location, radius: Int = 150): Location = with(strLoc) {
        var mX = x-radius
        while (mX < x+radius) {
            for (mY in 25..49) {
                var mZ = z-radius
                while (mZ < z+radius) {
                    val block: Block = world!!.getBlockAt(mX.toInt(), mY, mZ.toInt())
                    if (block.type == Material.COBBLESTONE_STAIRS) {
                        return block.location.clone().add(0, 1, 0)
                    }
                    mZ++
                }
            }
            mX++
        }
        return strLoc
    }

    @EventHandler
    fun onPortal(event: PlayerPortalEvent) = with(event) {
        if (GamePhaseManager.currentState == GameState.Stronghold && ingameNotFinished(player)) {
            cancel()
            finish(player.uniqueId)
            taskRunLater(5L) { player.teleport(from) } // Tp back because its broken
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onInteract(event: PlayerInteractEvent) = with(event) {
            if (item?.type == Material.ENDER_EYE && clickedBlock?.type != Material.END_PORTAL_FRAME) cancel()
            if (clickedBlock?.type == Material.IRON_DOOR && action == Action.RIGHT_CLICK_BLOCK) {
                clickedBlock!!.apply {
                    val data = (blockData as Openable)
                    data.isOpen = true
                    blockData = data
                    taskRunLater(20L) {
                        data.isOpen = false
                        blockData = data
                        player.playSound(location, Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 1F, 1F)
                    }
                    player.playSound(location, Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1F, 1F)
                }
            }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onBlockPlace(event: BlockPlaceEvent) = with(event) {
        if (blockAgainst.type == Material.END_PORTAL_FRAME) {
            isCancelled = false
            setBuild(true)
        }
    }
}