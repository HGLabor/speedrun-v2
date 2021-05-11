package de.hglabor.speedrun.utils

import de.hglabor.speedrun.player.UserList
import de.hglabor.speedrun.scoreboard.SpeedrunScoreboard
import de.hglabor.speedrun.worlds.Worlds
import org.bukkit.*
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

fun Entity.isCreative(): Boolean = this is Player && this.gameMode == GameMode.CREATIVE

fun Cancellable.cancel() {
    this.isCancelled = true
}

fun Player.clearInv() {
    this.inventory.clear()
}

fun Player.noMove(seconds: Int) {
    this.addPotionEffect(PotionEffect(PotionEffectType.SLOW, seconds*20, 6, true, false))
    this.addPotionEffect(PotionEffect(PotionEffectType.JUMP, seconds*20, 250, true, false))
}

fun Player.closeAndClearInv() {
    this.closeInventory()
    this.clearInv()
}

fun Player.updateScoreboard() {
    SpeedrunScoreboard.update(UserList[this.uniqueId]!!)
}

fun Player.createScoreboard() {
    SpeedrunScoreboard.create(UserList[this.uniqueId]!!)
}

fun List<ItemStack>.addToInv(player: Player) {
    player.addToInv(this)
}

fun Player.addToInv(items: List<ItemStack>) {
    items.forEach { this.inventory.addItem(it) }
}

fun Player.teleportToWorld(worldName: String) {
    this.teleport(Worlds[worldName]!!.spawnLocation)
}

fun Player.survival() { this.gameMode = GameMode.SURVIVAL }
fun Player.spectator() { this.gameMode = GameMode.SPECTATOR }

fun Material.stack(): ItemStack = ItemStack(this)
fun Material.stack(amount: Int): ItemStack = ItemStack(this, amount)

fun World.speedrunGameRules(): World {
    this.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
    this.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
    this.setGameRule(GameRule.DO_WEATHER_CYCLE, false)
    this.setGameRule(GameRule.DO_MOB_SPAWNING, false)
    this.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false)
    this.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true)
    return this
}

// Easy string colors

fun String.col(vararg colorNames: String): String {
    var prefix = ""
    colorNames.forEach { prefix += colorFromName(it) }
    return prefix + this + ChatColor.RESET.toString() + ChatColor.WHITE.toString()
}

fun colorFromName(name: String): ChatColor = ChatColor.valueOf(name.toUpperCase())