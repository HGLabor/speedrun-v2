package de.hglabor.speedrun.worlds

import de.hglabor.speedrun.game.GameState
import de.hglabor.speedrun.utils.speedrunGameRules
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator

object Worlds : HashMap<String, World>() {
    var created = false
    fun createWorlds() {
        if (created) return
        created = true
        GameState.values().forEach {
            val world = Bukkit.createWorld(WorldCreator(it.name.toLowerCase()))!!.speedrunGameRules()
            this[world.name] = world
        }
    }

    override fun get(key: String): World? = super.get(key.toLowerCase())
}