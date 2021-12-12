package de.hglabor.speedrun

import de.hglabor.speedrun.command.commands
import de.hglabor.speedrun.config.Config
import de.hglabor.speedrun.game.phase.GamePhaseManager
import de.hglabor.speedrun.game.phase.phases.crafting.CraftingUtils
import de.hglabor.speedrun.listener.*
import de.hglabor.speedrun.player.PlayerVisibility
import de.hglabor.speedrun.player.UserList
import de.hglabor.speedrun.utils.updateScoreboard
import de.hglabor.speedrun.worlds.Worlds
import de.hglabor.speedrun.worlds.structures
import net.axay.kspigot.main.KSpigot

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

        commands()

        PlayerVisibility
        CraftingUtils

        Worlds.createWorlds()
        structures()

        GamePhaseManager.start()
    }

    /*override fun getDefaultWorldGenerator(worldName: String, id: String?): ChunkGenerator = WorldGeneratorApi
        .getInstance(this, 0, 6)
        .createCustomGenerator(WorldRef.ofName("stronghold")) {
            it.baseTerrainGenerator = FlatDiamondGenerator()
            // Disable all decorations
            it.worldDecorator.withoutAllDefaultDecorations()
            // Enable strongholds again afterwards so only strongholds are enabled
            it.worldDecorator.setDefaultDecoratorsEnabled(DecorationType.STRONGHOLDS, true)
            logger.info("Enabled flat diamond generator for world \"$worldName\".")
        }*/

    fun updateScoreboards() {
        UserList.players.forEach { it.updateScoreboard() }
    }

    override fun shutdown() {}
}


