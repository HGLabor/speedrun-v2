package de.hglabor.speedrun.game.phase.phases

import de.hglabor.speedrun.config.Config
import de.hglabor.speedrun.game.GameState
import de.hglabor.speedrun.game.phase.GamePhase
import de.hglabor.speedrun.game.phase.GamePhaseManager
import de.hglabor.speedrun.player.UserList
import de.hglabor.speedrun.worlds.BLAZES_SPAWNS_LIST
import de.hglabor.utils.kutils.*
import de.hglabor.utils.noriskutils.SoundUtils
import net.axay.kspigot.extensions.geometry.add
import net.axay.kspigot.extensions.geometry.vec
import net.axay.kspigot.runnables.taskRunLater
import org.bukkit.*
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Blaze
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import java.util.*

class BlazesPhase : GamePhase(preparationDuration = 1, roundDuration = Config.BLAZES_INGAME_TIME.getInt()) {
    private var spawns: HashMap<UUID, Location> = HashMap()

    override fun startPreparationPhase() {}
    override fun startIngamePhase() {
        items()
        spawnTask()
    }

    private fun spawnTask() {
        taskRunLater(delay = (200..799).random().toLong()) {
            if (GamePhaseManager.currentState != GameState.Blazes) return@taskRunLater
            spawns.forEach { (_, location) ->
                val spawnerLoc = location.spawnerLocation()
                for (i in 0 until (Config.BLAZES_MIN_SPAWN.getInt()..Config.BLAZES_MAX_SPAWN.getInt()).random()) {
                    world.spawn(spawnerLoc.nextSpawnLoc(), Blaze::class.java)
                }
            }
            spawnTask()
        }
    }

    fun Location.nextSpawnLoc(): Location = clone()
        .add(vec((0..2).random(), (0..1).random(), (0..2).random())).apply {
            return if (!block.isEmpty) nextSpawnLoc() else this
        }

    override fun getScoreboardHeading(): String = "Points"
    override fun getScoreboardContent(): String = "${ChatColor.GOLD}0"
    override fun state() = GameState.Blazes

    private fun items() { UserList.players.forEach { items(it) } }
    private fun items(player: Player) {
        player.closeAndClearInv()
        player.inventory.addAll(listOf(Material.IRON_SWORD.stack(), Material.BOW.stack().apply { addEnchantment(Enchantment.ARROW_INFINITE, 1) }, Material.ARROW.stack()))
    }

    override fun onFall(player: Player) {
        player.teleport(spawns[player.uniqueId] ?: return)
    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageByEntityEvent) = with(event) {
        if (GamePhaseManager.currentState == GameState.Blazes) damager.sendMessage("you hit blaze :)")
    }

    private fun Location.spawnerLocation() = with(Config.BLAZES_SPAWNER_OFFSET.getIntList()) { add(get(0), get(1), get(2)) }

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
            //loc.x += 0.5
            loc.y = 20.0
            //loc.z += 0.5
            player.teleport(loc)
            SoundUtils.playTeleportSound(player)
        }
    }
}