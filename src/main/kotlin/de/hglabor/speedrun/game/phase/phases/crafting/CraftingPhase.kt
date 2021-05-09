package de.hglabor.speedrun.game.phase.phases.crafting

import de.hglabor.speedrun.game.GameState
import de.hglabor.speedrun.game.phase.GamePhase
import de.hglabor.speedrun.game.phase.GamePhaseManager
import de.hglabor.speedrun.player.UserList
import de.hglabor.speedrun.utils.*
import net.axay.kspigot.extensions.bukkit.actionBar
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.inventory.ItemStack


class CraftingPhase : GamePhase(ROUNDS, PREPARATION_DURATION, ROUND_DURATION) {
    companion object {
        const val ROUNDS: Long = 3
        const val ROUND_DURATION: Long = 30L // 30 seconds
        const val PREPARATION_DURATION: Long = 10L // 10 seconds
    }
    private var itemToCraft: ItemStack? = null

    override fun startPreparationPhase() {
        itemToCraft = CraftingUtils.randomItemToCraft.stack()
        UserList.players.forEach { player ->
            repeat(9) {
                player.inventory.setItem(it, itemToCraft)
            }
        }
    }

    override fun startIngamePhase() {
        UserList.players.forEach { CraftingUtils.getCraftingItems(itemToCraft!!.type).addToInv(it) }
    }

    override fun getGameState(): GameState = GameState.Crafting
    override fun getScoreboardHeading(): String = "Item:"
    override fun getScoreboardContent(): String = ChatColor.YELLOW.toString() + (itemToCraft?.type?.name ?: "Getting random item...")
    override fun onNewStart() {
        itemToCraft = null // make getScoreboardContent return "Getting random item..."
    }

    override fun onPrepStart() {
        UserList.players.forEach {
            it.sendTitle("§6Round $roundNumber", "§cCraft a §b${itemToCraft?.type?.name}", 20, 45, 20)
            it.actionBar("Good Luck!");
        }
    }

    /*@EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (GamePhaseManager.currentState == getGameState() && isPreparation()) {
            event.cancel()
        }
    }*/

    @EventHandler
    fun onCrafting(event: CraftItemEvent) {
        if (GamePhaseManager.currentState != GameState.Crafting) {
            return
        }
        if (event.recipe.result.isSimilar(ItemStack(itemToCraft!!.type))) {
            finish(event.whoClicked.uniqueId)
        }
    }
}
