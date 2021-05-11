package de.hglabor.speedrun.config

import de.hglabor.speedrun.PLUGIN
import org.bukkit.Bukkit
import org.bukkit.ChatColor

val PREFIX: String = "${ChatColor.DARK_GRAY}[${ChatColor.AQUA}Speedrun${ChatColor.DARK_GRAY}]${ChatColor.WHITE}"
val MAX_PLAYERS: Int = Bukkit.getServer().maxPlayers

enum class Config(private val path: String, private var value: Int) {
    MIN_PLAYERS("minPlayers", 4),
    CRAFTING_ROUNDS("craftingRounds", 5),
    CRAFTING_PREP_TIME("craftingPreparation", 5),
    CRAFTING_INGAME_TIME("craftingIngame", 15);

    companion object {
        fun load() {
            values().forEach { PLUGIN.config.addDefault(it.path, it.value) }
            PLUGIN.config.options().copyDefaults(true)
            PLUGIN.saveConfig()
        }
    }

    fun set(value: Int) {
        this.value = value
        PLUGIN.config.set(this.path, this.value)
        PLUGIN.saveConfig()
    }

    fun get(): Int = this.value

    fun reload() { PLUGIN.reloadConfig() }
}