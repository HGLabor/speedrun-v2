package de.hglabor.speedrun.game.phase

import de.hglabor.speedrun.game.GamePhase
import de.hglabor.speedrun.player.UserList
import org.bukkit.inventory.ItemStack

class CraftingPhase : GamePhase() {
    private lateinit var itemToCraft: ItemStack

    override fun startRound() {
        for (player in UserList.players) {
            player.closeInventory()
            player.inventory.clear()
            repeat(9) {
                player.inventory.addItem(itemToCraft)
            }
        }
    }
}
