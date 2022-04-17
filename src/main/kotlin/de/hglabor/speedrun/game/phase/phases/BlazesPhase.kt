package de.hglabor.speedrun.game.phase.phases

import de.hglabor.speedrun.config.Config
import de.hglabor.speedrun.config.PREFIX
import de.hglabor.speedrun.game.GameState
import de.hglabor.speedrun.game.phase.GamePhase
import de.hglabor.speedrun.game.phase.GamePhaseManager
import de.hglabor.speedrun.player.SpeedRunner
import de.hglabor.speedrun.player.UserList
import de.hglabor.speedrun.worlds.BLAZES_SPAWNS_LIST
import de.hglabor.utils.kutils.*
import de.hglabor.utils.noriskutils.SoundUtils
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.extensions.bukkit.actionBar
import net.axay.kspigot.extensions.geometry.add
import net.axay.kspigot.extensions.geometry.vec
import net.axay.kspigot.runnables.taskRunLater
import org.bukkit.*
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import java.util.*
import kotlin.math.max

class BlazesPhase : GamePhase(preparationDuration = 1, roundDuration = Config.BLAZES_INGAME_TIME.getInt()) {
    private var spawns: HashMap<UUID, Location> = HashMap()

    private val pointsMap = HashMap<UUID, Int>()
    private var Player.points: Int
        set(value) {
            pointsMap[uniqueId] = max(value, 0)
            if (value == Config.BLAZES_POINTS_NEEDED.getInt()) finish(uniqueId)
        }
        get() = pointsMap.getOrPut(uniqueId) { 0 }

    override fun startPreparationPhase() {}
    override fun startIngamePhase() {
        items()
        spawnTask()
    }

    override fun broadcastRoundInfo() {
        grayBroadcast("$PREFIX Get ${KColors.GOLD}10 ${KColors.WHITE}points!")
    }

    /*
    Normal times: 200 - 799 (10s - 40s)
    Our times: 200 - 300 (5s - 15s)
     */
    private fun spawnTask() {
        taskRunLater(delay = (200..300).random().toLong()) {
            if (GamePhaseManager.currentState != GameState.Blazes) return@taskRunLater
            val blazeAmount = (Config.BLAZES_MIN_SPAWN.getInt()..Config.BLAZES_MAX_SPAWN.getInt()).random()
            spawns.forEach { (_, location) ->
                val spawnerLoc = location.spawnerLocation()
                for (i in 0 until blazeAmount) world.spawn(spawnerLoc.nextSpawnLoc(), Blaze::class.java)
            }
            spawnTask()
        }
    }

    private fun Location.nextSpawnLoc(): Location = clone()
        .add(vec((0..2).random(), (0..1).random(), (0..2).random())).apply {
            return if (!block.isEmpty) nextSpawnLoc() else this
        }

    override fun getScoreboardHeading(): String = "Points"
    override fun getScoreboardContent(player: SpeedRunner): String = "${ChatColor.GOLD}${pointsMap[player.player.uniqueId] ?: 0}"
    override fun state() = GameState.Blazes

    private fun items() { UserList.players.forEach { items(it) } }
    private fun items(player: Player) {
        player.closeAndClearInv()
        player.inventory.addAll(listOf(Material.IRON_SWORD.stack(), Material.BOW.stack().apply { addEnchantment(Enchantment.ARROW_INFINITE, 1) }, Material.ARROW.stack()))
    }

    override fun onFall(player: Player) {
        player.teleport(spawns[player.uniqueId] ?: return)
    }

    override fun buildingAllowed(evt: BlockEvent) = evt is BlockBreakEvent && evt.block.type == Material.FIRE

    override fun onDamage(player: Player, evt: EntityDamageEvent): Boolean {
        return if (evt is EntityDamageByEntityEvent && evt.damager is Blaze || evt.cause == EntityDamageEvent.DamageCause.PROJECTILE || evt.cause == EntityDamageEvent.DamageCause.FIRE_TICK) {
            evt.damage = 0.0
            player.fireTicks = 0
            player.actionBar("${KColors.GOLD}${KColors.BOLD}-1")
            player.points--
            true
        }
        else false
    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageByEntityEvent) = with(event) {
        if (GamePhaseManager.currentState != GameState.Blazes || entity !is Blaze) return@with
        if (damager is Projectile && (damager as Projectile).shooter is Player) {
            val player = (damager as Projectile).shooter as Player
            if ((entity as Blaze).health - finalDamage <= 0) {
                player.actionBar("${KColors.GOLD}${KColors.BOLD}+1")
                player.playSound(Sound.ENTITY_PLAYER_LEVELUP, pitch = 0f)
                player.points++
            }
        }
        else if (damager is Player && (entity as Blaze).health - finalDamage <= 0) {
            val player = damager as Player
            player.actionBar("${KColors.GOLD}${KColors.BOLD}+1")
            player.playSound(Sound.ENTITY_PLAYER_LEVELUP, pitch = 0f)
            player.points++
        }
    }

    private fun Location.spawnerLocation() = with(Config.BLAZES_SPAWNER_OFFSET.getIntList()) { clone().add(get(0), get(1), get(2)) }

    override fun tpPlayers() {
        val shuffled = BLAZES_SPAWNS_LIST.shuffled()
        UserList.players.forEachIndexed { index, player ->
            spawns[player.uniqueId] = shuffled[index]
            val loc = spawns[player.uniqueId] ?: return
            val spawner = loc.spawnerLocation().block
            if (spawner.type != Material.SPAWNER) {
                logger.warning("Wrong spawner offset / no spawner at that location")
                return
            }
            loc.x += 0.5
            loc.y = 20.0
            loc.z += 0.5
            player.teleport(loc)
            SoundUtils.playTeleportSound(player)
        }
    }
}