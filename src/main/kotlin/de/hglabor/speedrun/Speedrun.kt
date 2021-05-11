package de.hglabor.speedrun

import de.hglabor.speedrun.command.NextPhaseCommand
import de.hglabor.speedrun.command.ReloadCommand
import de.hglabor.speedrun.config.Config
import de.hglabor.speedrun.game.phase.GamePhaseManager
import de.hglabor.speedrun.listener.joinListener
import de.hglabor.speedrun.listener.lobbyListener
import de.hglabor.speedrun.listener.mainListener
import de.hglabor.speedrun.listener.quitListener
import de.hglabor.speedrun.location.LOBBY_SPAWN
import de.hglabor.speedrun.player.UserList
import de.hglabor.speedrun.utils.clearInv
import de.hglabor.speedrun.utils.survival
import de.hglabor.speedrun.utils.updateScoreboard
import de.hglabor.speedrun.worlds.Worlds
import net.axay.kspigot.event.listen
import net.axay.kspigot.main.KSpigot
import org.bukkit.event.player.PlayerJoinEvent

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

        getCommand("next")?.setExecutor(NextPhaseCommand())
        getCommand("speedrun-reload")?.setExecutor(ReloadCommand())

        Worlds.createWorlds()

        listen<PlayerJoinEvent> {
            it.player.teleport(LOBBY_SPAWN)
            it.player.survival()
            it.player.clearInv()
        }

        GamePhaseManager.start()
    }

    fun updateScoreboards() {
        UserList.players.forEach { it.updateScoreboard() }
    }
    override fun shutdown() {}
}


