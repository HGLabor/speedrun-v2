package de.hglabor.speedrun.game.phase.phases

import de.hglabor.speedrun.config.Config
import de.hglabor.speedrun.config.PREFIX
import de.hglabor.speedrun.game.GameState
import de.hglabor.speedrun.game.phase.GamePhase
import de.hglabor.speedrun.player.UserList
import de.hglabor.speedrun.utils.*
import de.hglabor.speedrun.worlds.Worlds
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.extensions.bukkit.actionBar
import net.axay.kspigot.extensions.bukkit.title
import net.axay.kspigot.items.*
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.*
import org.bukkit.inventory.ItemFlag
import java.util.*


class CrystalPhase : GamePhase(preparationDuration = 1, roundDuration = Config.CRYSTAL_INGAME_TIME.getInt()) {
    private val crystalBlocks = HashMap<UUID, Set<Block>>()
    private val playerScores = HashMap<UUID, ArrayList<UUID>>()

    private val bow = itemStack(Material.BOW) {
        addEnchantment(Enchantment.ARROW_INFINITE, 1)
        addEnchantment(Enchantment.ARROW_DAMAGE, 1)
        meta {
            name = "${KColors.AQUA}Bow"
            isUnbreakable = true
            flag(ItemFlag.HIDE_UNBREAKABLE)
        }
    }

    override fun onNewStart() {
        UserList.players.forEach {
            playerScores[it.uniqueId] = ArrayList()
        }
    }

    private fun Player.finished() = playerScores[uniqueId]?.size == crystalBlocks.size
    private fun Player.addShotCrystal(uuid: UUID): Boolean {
        if (playerScores[uniqueId]!!.contains(uuid)) return false
        playerScores[uniqueId]!! += uuid
        return true
    }

    override fun startPreparationPhase() {
        scanCrystalBlocks()
        UserList.players.forEach { player ->
            player.title("${KColors.RED}Shoot all ${KColors.LIGHTPURPLE}Crystals", fadeIn = 20, stay = 40, fadeOut = 20)
            player.actionBar("Good Luck!")
        }
    }

    override fun startIngamePhase() {
        items()
    }

    override fun getScoreboardHeading(): String = "Amount"
    override fun getScoreboardContent(): String = "${ChatColor.GOLD}${crystalBlocks.size}"
    override fun getGameState(): GameState = GameState.Crystal
    override fun broadcastRoundInfo() {
        grayBroadcast("$PREFIX Destroy all ${crystalBlocks.size.toString().col("aqua")} Crystals")
    }

    private fun items() { UserList.players.forEach { items(it) } }
    private fun items(player: Player) {
        player.closeAndClearInv()
        player.addToInv(listOf(bow, Material.ARROW.stack()))
    }


    private fun scanCrystalBlocks() {
        val crystals: Collection<Entity> = Worlds["crystal"]!!.getEntitiesByClasses(EnderCrystal::class.java)
        for (crystal in crystals) {
            crystalBlocks[crystal.uniqueId] = scanObsidianBlocks(crystal.location)
        }
    }

    private fun scanObsidianBlocks(start: Location, r: Int = 5): Set<Block> {
        return start.world!!.blocksBetween(start.blockX-r, start.blockX+r, start.blockZ-r, start.blockZ+r, y2 = start.blockY).scanFor(Material.OBSIDIAN)
    }

    @EventHandler
    fun crystalDamage(event: EntityDamageByEntityEvent) {
        if (event.entity !is EnderCrystal) return
        if (event.damager !is Projectile) return
        if ((event.damager as Projectile).shooter !is Player) return
        event.cancel()

        val player: Player = (event.damager as Projectile).shooter as Player
        if (!player.addShotCrystal(event.entity.uniqueId)) {
            // Player has shot a crystal twice
            player.playPlingSound(0)
            return
        }

        player.playPlingSound()

        crystalBlocks[event.entity.uniqueId]?.forEach {
            player.sendBlockChange(it.location, Material.EMERALD_BLOCK.createBlockData())
        }

        if (player.finished()) finish(player.uniqueId)
    }

    @EventHandler
    fun enderManTarget(event: EntityTargetEvent) {
        if (event.entity is Enderman) event.cancel()
    }

    @EventHandler
    fun entitySpawn(event: EntitySpawnEvent) {
        if (event.entity is EnderDragon) event.cancel()
    }
}