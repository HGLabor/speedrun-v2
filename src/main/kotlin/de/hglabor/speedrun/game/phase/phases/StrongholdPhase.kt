package de.hglabor.speedrun.game.phase.phases

import de.hglabor.speedrun.config.Config
import de.hglabor.speedrun.game.GameState
import de.hglabor.speedrun.game.phase.GamePhase
import de.hglabor.speedrun.game.phase.GamePhaseManager
import de.hglabor.speedrun.player.SpeedRunner
import de.hglabor.speedrun.player.UserList
import de.hglabor.utils.kutils.cancel
import de.hglabor.utils.kutils.playSound
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
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack


class StrongholdPhase : GamePhase(preparationDuration = 1, roundDuration = Config.STRONGHOLD_INGAME_TIME.getInt()) {
    private lateinit var spawnLoc: Location

    override fun startPreparationPhase() {}
    override fun startIngamePhase() { items() }

    override fun getScoreboardHeading(): String = "Stuck?"
    override fun getScoreboardContent(player: SpeedRunner): String = "${ChatColor.GOLD}/renew"
    override fun state() = GameState.Stronghold

    private fun items() { UserList.players.forEach { items(it) } }
    private fun items(player: Player) {
        player.inventory.addItem(ItemStack(Material.ENDER_EYE, 64))
    }

    override fun tpPlayers() = with(GameState.Stronghold.world) {
        UserList.players.forEach { it.teleport(this.spawnLocation) }
        taskRunLater(10L) {
            // Official Minecraft Wiki:
            // All strongholds are located at random coordinates within rings in most biomes,
            // where each ring is a certain radius from the center of the world (X=0, Z=0)
            // The first ring has 3 strongholds within 1,280-2,816 blocks of the origin
            val strongholdLoc: Location = locateNearestStructure(Location(world, 0.0, 0.0, 0.0), StructureType.STRONGHOLD, 2816, false)!!
            spawnLoc = getSpawnLoc(strongholdLoc)
            UserList.players.forEach { it.teleport(spawnLoc) }
        }
    }

    override fun onRenew(player: Player): Boolean {
        if (this::spawnLoc.isInitialized) {
            player.teleport(spawnLoc)
            return true
        }
        return false
    }

    // Cancel tp to spawn when moving under y 10
    override fun onFall(player: Player) {}

    private fun getSpawnLoc(strLoc: Location, radius: Int = 20): Location = with(strLoc) {
        for (mX in x.toInt()-radius..x.toInt()+radius) for (mY in -50..50) for (mZ in z.toInt()-radius..z.toInt()+radius) {
                    val block: Block = world!!.getBlockAt(mX, mY, mZ)
                    if (block.type == Material.SMOOTH_STONE_SLAB) return block.location.clone().add(0, 1, 0)
                }
        return strLoc
    }

    @EventHandler
    fun onPortal(event: PlayerPortalEvent) = with(event) {
        if (GamePhaseManager.currentState != GameState.Stronghold) return
        if (cause != PlayerTeleportEvent.TeleportCause.END_PORTAL) return
        cancel()
        if(!ingameNotFinished(player)) return
        finish(player.uniqueId)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onInteract(event: PlayerInteractEvent) = with(event) {
            if (item?.type == Material.ENDER_EYE && clickedBlock?.type != Material.END_PORTAL_FRAME) cancel()
            if (clickedBlock?.type == Material.IRON_DOOR && action == Action.RIGHT_CLICK_BLOCK) {
                if (player.gameMode == GameMode.SPECTATOR) return
                clickedBlock!!.apply {
                    val data = (blockData as Openable)
                    if (data.isOpen) return // Cancel clicks on open iron doors
                    data.isOpen = true
                    blockData = data
                    taskRunLater(20L) {
                        data.isOpen = false
                        blockData = data
                        player.playSound(Sound.BLOCK_IRON_DOOR_OPEN)
                    }
                    player.playSound(Sound.BLOCK_IRON_DOOR_CLOSE)
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