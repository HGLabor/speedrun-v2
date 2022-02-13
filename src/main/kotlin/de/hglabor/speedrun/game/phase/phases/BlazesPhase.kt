package de.hglabor.speedrun.game.phase.phases

import de.hglabor.speedrun.config.Config
import de.hglabor.speedrun.game.GameState
import de.hglabor.speedrun.game.phase.GamePhase
import de.hglabor.speedrun.game.phase.GamePhaseManager
import de.hglabor.speedrun.player.UserList
import de.hglabor.speedrun.worlds.*
import de.hglabor.utils.kutils.*
import de.hglabor.utils.noriskutils.SoundUtils
import org.bukkit.*
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import java.util.*

class BlazesPhase : GamePhase(preparationDuration = 1, roundDuration = Config.BLAZES_INGAME_TIME.getInt()) {
    private var spawns: HashMap<UUID, Location> = HashMap()

    override fun startPreparationPhase() {}
    override fun startIngamePhase() { items() }

    override fun getScoreboardHeading(): String = "Reset"
    override fun getScoreboardContent(): String = "${ChatColor.GOLD}/renew"
    override fun state() = GameState.Portal

    private fun items() { UserList.players.forEach { items(it) } }
    private fun items(player: Player) {
        player.closeAndClearInv()
        player.inventory.addAll(listOf(Material.IRON_SWORD.stack(), Material.BOW.stack().apply { addEnchantment(Enchantment.ARROW_INFINITE, 0) }, Material.ARROW.stack()))
    }

    override fun onRenew(player: Player): Boolean {
        // When portal spawns are non-null, the clipboard is probably non-null too
        if (!ingameNotFinished(player)) return false
        spawns[player.uniqueId]?.let {
            pasteClipboard(it, blazesClipboard()!!)
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
    fun onEntityDamage(event: EntityDamageByEntityEvent) = with(event) {
        if (GamePhaseManager.currentState == GameState.Blazes) damager.sendMessage("you hit blaze :)")
    }

    override fun tpPlayers() {
        val shuffled = BLAZES_SPAWNS_LIST.shuffled()
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