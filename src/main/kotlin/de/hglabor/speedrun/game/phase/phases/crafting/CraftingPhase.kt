package de.hglabor.speedrun.game.phase.phases.crafting

import de.hglabor.speedrun.config.Config
import de.hglabor.speedrun.config.PREFIX
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


class CraftingPhase : GamePhase(Config.CRAFTING_ROUNDS.getInt(), Config.CRAFTING_PREP_TIME.getInt(), Config.CRAFTING_INGAME_TIME.getInt()) {
    private var itemToCraft: ItemStack? = null


    override fun startPreparationPhase() {
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
    override fun getScoreboardContent(): String = ChatColor.YELLOW.toString() + (if (isPreparation()) itemToCraft?.type?.name else "Getting random item...")
    override fun onNewStart() {
        itemToCraft = CraftingUtils.randomItemToCraft.stack()
    }

    override fun onPrepStart() {
        UserList.players.forEach {
            it.sendTitle("§6Round $roundNumber", "§cCraft (a) §b${itemToCraft?.type?.name}", 20, 45, 20)
            it.actionBar("Good Luck!");
        }
    }

    override fun broadcastRoundInfo() {
        grayBroadcast("$PREFIX Item to craft: ${ChatColor.AQUA}${itemToCraft?.type?.name}")
    }

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
