package de.hglabor.speedrun.game.phase.phases

import de.hglabor.speedrun.PLUGIN
import de.hglabor.speedrun.config.Config
import de.hglabor.speedrun.config.PREFIX
import de.hglabor.speedrun.game.GameState
import de.hglabor.speedrun.game.phase.GamePhase
import de.hglabor.speedrun.game.phase.GamePhaseManager
import de.hglabor.speedrun.player.UserList
import de.hglabor.speedrun.utils.grayBroadcast
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.runnables.KSpigotRunnable
import net.axay.kspigot.runnables.task
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class LobbyPhase : GamePhase(0, -1, -1) {
    override fun startPreparationPhase() {}
    override fun startIngamePhase() {}
    override fun getScoreboardHeading(): String = "Leave"
    override fun getScoreboardContent(): String = "${ChatColor.RED}/hub"

    private var task: KSpigotRunnable? = null

    private var startingIn = -1
    set(value) {
        field = value
        when(value) {
            -1 -> {
                grayBroadcast("$PREFIX ${KColors.ORANGERED}Not enough players (${UserList.size}). Start cancelled")
                task?.cancel()
            }
            120, 90, 60, 30, 20, 10 -> announceTime()
        }
        time = startingIn.toLong()
        PLUGIN.updateScoreboards()
    }
    private val hasStarted get() = startingIn != -1

    private fun announceTime() {
        if (startingIn != 0) grayBroadcast("$PREFIX ${KColors.GREENYELLOW}Starting in ${KColors.GREEN}$startingIn ${KColors.GREENYELLOW} seconds.")
        else grayBroadcast("$PREFIX ${KColors.GREEN}Starting...")
    }

    override fun state() = GameState.Lobby
    override fun stop() { super.stop(); task?.cancel() }

    private fun task() {
        task?.cancel()
        task = task(period = 20L) {
            startingIn--
            if (startingIn <= 5) announceTime()
            if (startingIn == 0) GamePhaseManager.nextPhase()
        }
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (!hasStarted && UserList.size >= Config.MIN_PLAYERS.getInt()) {
            startingIn = Config.START_TIME_MIN_PLAYERS.getInt()
            task()
        }
        else if (UserList.size >= Config.MIN_PLAYERS.getInt()*2) {
            val doubleTime = Config.START_TIME_DOUBLE_MIN_PLAYERS.getInt()
            if (startingIn > doubleTime) {
                startingIn = doubleTime
            }
        }
        PLUGIN.updateScoreboards()
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        if (hasStarted && UserList.size < Config.MIN_PLAYERS.getInt()) startingIn = -1
    }

    override fun onRenew(player: Player): Boolean {
        player.tpSpawn()
        return true
    }
}