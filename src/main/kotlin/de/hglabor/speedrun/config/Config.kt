package de.hglabor.speedrun.config

import de.hglabor.speedrun.PLUGIN
import org.bukkit.Bukkit
import org.bukkit.ChatColor

val PREFIX: String = "${ChatColor.DARK_GRAY}[${ChatColor.AQUA}Speedrun${ChatColor.DARK_GRAY}]${ChatColor.WHITE}"
val MAX_PLAYERS: Int = Bukkit.getServer().maxPlayers

enum class Config(private val path: String, value: Any) {
    MIN_PLAYERS("minPlayers", 4),
    CRAFTING_ROUNDS("craftingRounds", 5),
    CRAFTING_PREP_TIME("craftingPreparation", 5),
    CRAFTING_INGAME_TIME("craftingIngame", 15),
    RESTART_TIME("restartAfter", 30),
    DO_RESTART("doRestart", true);

    private val mValue: Any get() = PLUGIN.config.get(this.path)!!;

    companion object {
        fun load() {
            values().forEach { PLUGIN.config.addDefault(it.path, it.mValue) }
            PLUGIN.config.options().copyDefaults(true)
            PLUGIN.saveConfig()
        }
    }

    fun set(value: Int) {
        PLUGIN.config.set(this.path, value)
        PLUGIN.saveConfig()
    }

    fun getInt(): Int = this.mValue as Int
    fun getBoolean(): Boolean = this.mValue as Boolean

    fun reload() { PLUGIN.reloadConfig() }
}