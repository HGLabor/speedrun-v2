package de.hglabor.speedrun.config

import de.hglabor.speedrun.PLUGIN
import org.bukkit.Bukkit
import org.bukkit.ChatColor

val PREFIX: String = "${ChatColor.DARK_GRAY}[${ChatColor.AQUA}Speedrun${ChatColor.DARK_GRAY}]${ChatColor.WHITE}"
val MAX_PLAYERS: Int = Bukkit.getServer().maxPlayers

enum class Config(private val path: String, value: Any) {
    MIN_PLAYERS("minPlayers", 4),
    BROADCAST_WINNERS_AMOUNT("winnersToBroadcast", 10),

    // Crafting
    CRAFTING_ROUNDS("craftingRounds", 5),
    CRAFTING_PREP_TIME("craftingPreparation", 5),
    CRAFTING_INGAME_TIME("craftingIngame", 15),

    // Portal
    PORTAL_INGAME_TIME("portalIngame", 60),

    // Stronghold
    STRONGHOLD_INGAME_TIME("strongholdIngame", 60),

    // Crystal
    CRYSTAL_INGAME_TIME("crystalIngame", 90),

    RESTART_TIME("restartAfter", 30),
    DO_RESTART("doRestart", true),

    ;

    private val configValue: Any get() = PLUGIN.config.get(this.path) ?: this.mValue
    private var mValue: Any = value

    companion object {
        fun load() {
            values().forEach { PLUGIN.config.addDefault(it.path, it.configValue) }
            PLUGIN.config.options().copyDefaults(true)
            PLUGIN.saveConfig()
        }
        fun reload() { PLUGIN.reloadConfig() }
    }

    fun set(value: Int) {
        mValue = value
        PLUGIN.config.set(this.path, value)
        PLUGIN.saveConfig()
    }

    fun getInt(): Int = this.configValue as Int
    fun getBoolean(): Boolean = this.configValue as Boolean
}