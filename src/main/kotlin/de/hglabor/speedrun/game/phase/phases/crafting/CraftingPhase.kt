package de.hglabor.speedrun.game.phase.phases.crafting

import de.hglabor.speedrun.config.Config
import de.hglabor.speedrun.config.PREFIX
import de.hglabor.speedrun.game.GameState
import de.hglabor.speedrun.game.phase.GamePhase
import de.hglabor.speedrun.game.phase.GamePhaseManager
import de.hglabor.speedrun.player.UserList
import de.hglabor.speedrun.utils.*
import de.hglabor.speedrun.worlds.CRAFTING_SPAWNS
import de.hglabor.utils.noriskutils.ItemBuilder
import de.hglabor.utils.noriskutils.SoundUtils
import net.axay.kspigot.extensions.bukkit.actionBar
import net.axay.kspigot.extensions.bukkit.title
import net.axay.kspigot.extensions.geometry.add
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.*
import org.bukkit.inventory.ItemStack


class CraftingPhase : GamePhase(Config.CRAFTING_ROUNDS.getInt(), Config.CRAFTING_PREP_TIME.getInt(), Config.CRAFTING_INGAME_TIME.getInt()) {
    private val items = ArrayList<Material>()
    private var itemToCraft: ItemStack? = null

    private fun newRandomItem(): Material  {
        val item = CraftingUtils.randomItem()
        return if (item !in items) item.apply { items += this }
        else newRandomItem()
    }

    override fun startPreparationPhase() {
        val material = newRandomItem()
        itemToCraft = ItemBuilder(material).setName(ChatColor.AQUA.toString() + material.name).build()
        UserList.players.forEach { player ->
            player.title("§6Round $roundNumber", "§cCraft (a) §b${itemToCraft?.type?.name}", 20, 40, 20)
            player.actionBar("Good Luck!")
            repeat(9) {
                player.inventory.setItem(it, itemToCraft)
            }
        }
    }

    override fun startIngamePhase() { UserList.players.forEach { items(it) } }

    private fun items(player: Player) {
        player.clearInv()
        CraftingUtils.getCraftingItems(itemToCraft!!.type).addToInv(player)
    }

    override fun onRenew(player: Player): Boolean {
        if (ingameNotFinished(player)) {
            items(player)
            return true
        }
        return false
    }

    override fun state() = GameState.Crafting
    override fun getScoreboardHeading(): String = "Item:"
    override fun getScoreboardContent(): String = ChatColor.YELLOW.toString() + (itemToCraft?.type?.name ?: "")
    override fun onNewStart() { itemToCraft = null }

    override fun broadcastRoundInfo() { grayBroadcast("$PREFIX Item to craft: ${ChatColor.AQUA}${itemToCraft?.type?.name}") }

    override fun tpPlayers() {
        val shuffled = CRAFTING_SPAWNS!!.shuffled()
        UserList.players.forEachIndexed { index, player ->
            player.teleport(shuffled[index].clone().add(0.5, 0, 0.5))
            SoundUtils.playTeleportSound(player)
        }
    }

    @EventHandler
    fun onCrafting(event: CraftItemEvent) = with(event) {
        if (GamePhaseManager.currentState != GameState.Crafting) return
        if (recipe.result.isSimilar(ItemStack(itemToCraft!!.type))) {
            finish(whoClicked.uniqueId)
        }
    }

    @EventHandler
    fun onOpenInventory(event: InventoryOpenEvent) = with(event) { if (!ingameNotFinished(player)) cancel() }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) = with(event) { if (!ingameNotFinished(whoClicked)) cancel() }
}
