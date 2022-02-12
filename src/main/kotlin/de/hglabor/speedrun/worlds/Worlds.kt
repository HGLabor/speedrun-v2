package de.hglabor.speedrun.worlds

import de.hglabor.speedrun.PLUGIN
import de.hglabor.speedrun.game.GameState
import de.hglabor.utils.kutils.trainingGameRules
import org.apache.commons.io.FileUtils
import org.bukkit.*
import java.io.File

object Worlds : HashMap<String, World>() {
    fun createWorlds() {
        deleteWorlds()
        GameState.values().forEach {
            val creator = WorldCreator(it.name.lowercase())
            when(it.name.lowercase()) {
                "crystal" -> creator.environment(World.Environment.THE_END)
            }
            val world = Bukkit.createWorld(creator)!!.apply {
                if (name.equals("crystal", true)) setSpawnLocation(0, 70, 0)
                trainingGameRules()
            }
            this[world.name] = world
        }
    }

    /** Delete worlds that have to be regenerated each time */
    private fun deleteWorlds() {
        deleteWorld("stronghold")
        deleteWorld("crystal")
    }

    private fun deleteWorld(worldName: String) {
        FileUtils.deleteDirectory(File(PLUGIN.server.worldContainer.absolutePath + "\\$worldName"))
    }

    override fun get(key: String): World? = super.get(key.lowercase())
}