package de.hglabor.speedrun.game.phase.phases.crafting

import de.hglabor.speedrun.config.Config
import de.hglabor.speedrun.config.PREFIX
import de.hglabor.speedrun.game.GameState
import de.hglabor.speedrun.game.phase.GamePhase
import de.hglabor.speedrun.game.phase.GamePhaseManager
import de.hglabor.speedrun.player.UserList
import de.hglabor.speedrun.utils.addToInv
import de.hglabor.speedrun.utils.cancel
import de.hglabor.speedrun.utils.grayBroadcast
import de.hglabor.speedrun.worlds.CRAFTING_SPAWNS
import de.hglabor.utils.noriskutils.ItemBuilder
import de.hglabor.utils.noriskutils.SoundUtils
import net.axay.kspigot.extensions.bukkit.actionBar
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemStack


class CraftingPhase : GamePhase(Config.CRAFTING_ROUNDS.getInt(), Config.CRAFTING_PREP_TIME.getInt(), Config.CRAFTING_INGAME_TIME.getInt()) {
    private var itemToCraft: ItemStack? = null

    override fun startPreparationPhase() {
        val material = CraftingUtils.randomItemToCraft
        itemToCraft = ItemBuilder(material).setName(ChatColor.AQUA.toString() + material.name).build()
        UserList.players.forEach { player ->
            player.sendTitle("§6Round $roundNumber", "§cCraft (a) §b${itemToCraft?.type?.name}", 20, 45, 20)
            player.actionBar("Good Luck!")
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
    override fun getScoreboardContent(): String = ChatColor.YELLOW.toString() + (itemToCraft?.type?.name ?: "")
    override fun onNewStart() { itemToCraft = null }

    override fun broadcastRoundInfo() {
        grayBroadcast("$PREFIX Item to craft: ${ChatColor.AQUA}${itemToCraft?.type?.name}")
    }

    override fun teleportPlayers() {
        val shuffled = CRAFTING_SPAWNS!!.shuffled()
        UserList.players.forEachIndexed { index, player ->
            player.teleport(shuffled[index])
            SoundUtils.playTeleportSound(player)
        }
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

    @EventHandler
    fun onOpenInventory(event: InventoryOpenEvent) { if (event.player.gameMode == GameMode.SPECTATOR || !isIngame()) event.cancel() }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) { if (event.whoClicked.gameMode == GameMode.SPECTATOR || !isIngame()) event.cancel() }
}
