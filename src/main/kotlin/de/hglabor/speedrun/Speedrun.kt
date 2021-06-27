package de.hglabor.speedrun

import de.hglabor.speedrun.command.*
import de.hglabor.speedrun.config.Config
import de.hglabor.speedrun.game.phase.GamePhaseManager
import de.hglabor.speedrun.game.phase.phases.crafting.CraftingUtils
import de.hglabor.speedrun.listener.*
import de.hglabor.speedrun.player.PlayerVisibility
import de.hglabor.speedrun.player.UserList
import de.hglabor.speedrun.utils.updateScoreboard
import de.hglabor.speedrun.worlds.Worlds
import de.hglabor.speedrun.worlds.generator.FlatDiamondGenerator
import de.hglabor.speedrun.worlds.structures
import net.axay.kspigot.extensions.bukkit.register
import net.axay.kspigot.main.KSpigot
import nl.rutgerkok.worldgeneratorapi.WorldGeneratorApi
import nl.rutgerkok.worldgeneratorapi.WorldRef
import nl.rutgerkok.worldgeneratorapi.decoration.DecorationType
import org.bukkit.generator.ChunkGenerator

val PLUGIN by lazy { Speedrun.INSTANCE }

class Speedrun : KSpigot() {
    companion object {
        lateinit var INSTANCE: Speedrun; private set
    }

    override fun load() {
        INSTANCE = this
    }

    override fun startup() {
        Config.load()
        UserList.init()

        mainListener()
        lobbyListener()
        joinListener()
        quitListener()

        StartCommand.register("start")
        NextPhaseCommand.register("next")
        ReloadCommand.register("speedrun-reload")
        LoadStructuresCommand.register("loadstructures")
        RenewCommand.register("renew")

        PlayerVisibility
        CraftingUtils

        Worlds.createWorlds()
        structures()

        GamePhaseManager.start()
    }

    override fun getDefaultWorldGenerator(worldName: String, id: String?): ChunkGenerator? {
        return WorldGeneratorApi
            .getInstance(this, 0, 5)
            .createCustomGenerator(WorldRef.ofName("stronghold")) {
                it.baseTerrainGenerator = FlatDiamondGenerator()
                // Disable all decorations
                it.worldDecorator.withoutAllDefaultDecorations()
                // Enable strongholds again afterwards so only strongholds are enabled
                it.worldDecorator.setDefaultDecoratorsEnabled(DecorationType.STRONGHOLDS, true)
            }
    }

    fun updateScoreboards() {
        UserList.players.forEach { it.updateScoreboard() }
    }

    override fun shutdown() {}
}


