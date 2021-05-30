package de.hglabor.speedrun.worlds

import de.hglabor.speedrun.PLUGIN
import de.hglabor.speedrun.game.GameState
import de.hglabor.speedrun.utils.speedrunGameRules
import org.bukkit.*
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils
import java.io.File

object Worlds : HashMap<String, World>() {
    fun createWorlds() {
        deleteWorlds()
        GameState.values().forEach {
            val world = Bukkit.createWorld(WorldCreator(it.name.toLowerCase()).environment(getEnv(it.name)))!!.speedrunGameRules()
            this[world.name] = world
        }
    }

    private fun getEnv(worldName: String): World.Environment = if (worldName.toLowerCase() == "crystal") World.Environment.THE_END else World.Environment.NORMAL

    /** Delete worlds that have to be regenerated each time */
    private fun deleteWorlds() {
        deleteWorld("stronghold")
        deleteWorld("crystal")
    }

    private fun deleteWorld(worldName: String) {
        FileUtils.deleteDirectory(File(PLUGIN.server.worldContainer.absolutePath + "\\$worldName"))
    }

    override fun get(key: String): World? = super.get(key.toLowerCase())
}