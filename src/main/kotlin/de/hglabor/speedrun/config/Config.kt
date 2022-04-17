package de.hglabor.speedrun.config

import de.hglabor.speedrun.PLUGIN
import org.bukkit.Bukkit
import org.bukkit.ChatColor

val PREFIX: String = "${ChatColor.DARK_GRAY}[${ChatColor.AQUA}Speedrun${ChatColor.DARK_GRAY}]${ChatColor.WHITE}"
val MAX_PLAYERS: Int = Bukkit.getServer().maxPlayers

enum class Config(private val path: String, value: Any) {
    MIN_PLAYERS("minPlayers", 4),

    START_TIME_MIN_PLAYERS("startTimeMinPlayers", 120),
    START_TIME_DOUBLE_MIN_PLAYERS("startTimeDoubleMinPlayers", 20),
    START_TIME_START_COMMAND("startTimeStartCommand", 10),

    BROADCAST_WINNERS_AMOUNT("winnersToBroadcast", 10),

    // Crafting
    CRAFTING_ROUNDS("craftingRounds", 5),
    CRAFTING_PREP_TIME("craftingPreparation", 5),
    CRAFTING_INGAME_TIME("craftingIngame", 15),
    CRAFTING_ITEMS("craftingItems", arrayListOf<String>()),

    // Portal
    PORTAL_INGAME_TIME("portalIngame", 60),
    PORTAL_SCHEMATIC("portalSchem", "lavaArena_03"),

    // Blazes
    BLAZES_INGAME_TIME("blazesIngame", 120),
    BLAZES_SCHEMATIC("blazesSchem", "blazeSpawner_02"),
    BLAZES_SPAWNER_OFFSET("blazesSchemSpawnerOffset", listOf(0, 3, -9)), // x, y, z offset from spawn location to spawner location
    BLAZES_MIN_SPAWN("blazesMinSpawn", 1),
    BLAZES_MAX_SPAWN("blazesMaxSpawn", 3),
    BLAZES_POINTS_NEEDED("blazesPointsNeeded", 10),

    // Stronghold
    STRONGHOLD_INGAME_TIME("strongholdIngame", 60),

    // Crystal
    CRYSTAL_INGAME_TIME("crystalIngame", 90),

    RESTART_TIME("restartAfter", 15),
    CANCEL_RESTART_TIME("cancelRestartAfter", 2),

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
    fun getString(): String = this.configValue as String
    @Suppress("UNCHECKED_CAST") fun getStringList(): ArrayList<String> = this.configValue as ArrayList<String>? ?: ArrayList()
    @Suppress("UNCHECKED_CAST") fun getIntList(): List<Int> = this.configValue as List<Int>? ?: listOf()
}