package de.hglabor.speedrun.player

import de.hglabor.utils.kutils.cancel
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.bukkit.hideOnlinePlayers
import net.axay.kspigot.extensions.bukkit.showOnlinePlayers
import net.axay.kspigot.items.*
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack

object PlayerVisibility {
    const val SLOT = 17

    private object Materials {
        val visible = Material.SOUL_TORCH
        val hidden = Material.REDSTONE_TORCH
    }

    private object Text {
        val players = "${KColors.AQUA}Players:"
        val visible = "$players ${KColors.GREEN}VISIBLE"
        val hidden =  "$players ${KColors.ORANGE}HIDDEN"
    }

    private val ITEM: ItemStack = itemStack(Material.SOUL_TORCH) {
        meta { name = Text.visible}
    }

    init {
        listen<PlayerJoinEvent> {
            it.player.inventory.setItem(SLOT, ITEM)
        }

        listen<InventoryClickEvent> { with(it) {
            if (whoClicked !is Player || !currentItem.isToggleItem()) return@listen

            cancel()
            currentItem.toggle()
            if (currentItem!!.visible) (it.whoClicked as Player).showOnlinePlayers()
            else (it.whoClicked as Player).hideOnlinePlayers()
        }}
    }

    private fun ItemStack?.isToggleItem(): Boolean = this?.itemMeta?.name?.contains(Text.players) == true
    private fun ItemStack?.toggle() {
        this!!
        if (!isToggleItem()) return

        this.type = if (visible) Materials.hidden else Materials.visible // call this before the line below !
        this.meta { name = if (visible) Text.hidden else Text.visible }
    }
    private val ItemStack.visible get() = this.itemMeta?.name == Text.visible
}