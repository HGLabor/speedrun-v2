package de.hglabor.speedrun

import de.hglabor.speedrun.command.commands
import de.hglabor.speedrun.config.Config
import de.hglabor.speedrun.database.SpeedrunDB
import de.hglabor.speedrun.game.phase.GamePhaseManager
import de.hglabor.speedrun.listener.*
import de.hglabor.speedrun.player.PlayerVisibility
import de.hglabor.speedrun.player.UserList
import de.hglabor.speedrun.utils.updateScoreboard
import de.hglabor.speedrun.worlds.Worlds
import de.hglabor.speedrun.worlds.generator.StrongholdGenerator
import de.hglabor.speedrun.worlds.structures
import de.hglabor.utils.kutils.CraftingUtils
import net.axay.kspigot.main.KSpigot
import org.bukkit.Bukkit

val PLUGIN by lazy { Speedrun.INSTANCE }

class Speedrun : KSpigot() {
    companion object {
        lateinit var INSTANCE: Speedrun; private set
    }

    override fun load() {
        INSTANCE = this
        System.setProperty(
            "org.litote.mongo.test.mapping.service",
            "org.litote.kmongo.serialization.SerializationClassMappingTypeService"
        )
    }

    override fun startup() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord")

        Config.load()
        Worlds.createWorlds()
        SpeedrunDB.enable()

        UserList.init()

        mainListener()
        joinListener()
        quitListener()

        commands()

        PlayerVisibility
        CraftingUtils

        structures()

        GamePhaseManager.start()
    }

    fun updateScoreboards() {
        UserList.players.forEach { it.updateScoreboard() }
    }

    override fun getDefaultWorldGenerator(worldName: String, id: String?) = StrongholdGenerator()


    override fun shutdown() {
        SpeedrunDB.disable()
    }
}


