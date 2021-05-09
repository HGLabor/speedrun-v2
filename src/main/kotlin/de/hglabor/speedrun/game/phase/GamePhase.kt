package de.hglabor.speedrun.game.phase

import de.hglabor.speedrun.PLUGIN
import de.hglabor.speedrun.game.GameState
import de.hglabor.speedrun.player.UserList
import de.hglabor.speedrun.utils.noMove
import de.hglabor.speedrun.utils.teleportToWorld
import net.axay.kspigot.event.register
import net.axay.kspigot.event.unregister
import net.axay.kspigot.runnables.KSpigotRunnable
import net.axay.kspigot.runnables.task
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.Listener
import java.util.*

abstract class GamePhase(private var rounds: Long, private var preparationDuration: Long, private var roundDuration: Long) : Listener {
    private var activePhase = Phase.PREPARATION
    private var currentTask: KSpigotRunnable? = null
    var startTime = 20
    private var roundCounter: Long = 0
    private var finishedPlayers = ArrayList<UUID>()
    var timeHeading: String = "Starting in:"
    var time: Long = 0L

    private val startDuration: Long = 3 // 3 seconds

    enum class Phase {
        PREPARATION, INGAME
    }

    init {
        register() // Register this as event listener

        rounds *= 2

        UserList.players.forEach {
            it.sendMessage("Teleporting you to " + getGameState().name + " world")
            it.teleportToWorld(getGameState().name)
        }
        if (roundDuration != -1L) {
            startRoundTask()
        }
    }

    private fun startRoundTask() {
        val wholeDuration = startDuration+preparationDuration+roundDuration
        currentTask?.cancel()
        currentTask = task(
            howOften = wholeDuration+1, // Stop everything in round wholeDuration+1 and rerun
            period = 20L // Every second
        ) {
            val counterLong = it.counterUp!!.toLong()
            val ingameStart = startDuration+preparationDuration
            when (counterLong) {
                1L -> {
                    UserList.clearAndCloseAllInvs()
                    onNewStart()
                    UserList.players.forEach { player -> player.noMove(3) }
                    Bukkit.broadcastMessage("${ChatColor.GREEN}Starting in 3 seconds.")
                    timeHeading = "Starting in:"
                }
                startDuration -> {
                    Bukkit.broadcastMessage("${ChatColor.GREEN}Starting.")
                    UserList.clearAndCloseAllInvs()
                    startPreparationPhase()
                    timeHeading = "Preparation Time:"
                }
                ingameStart -> {
                    Bukkit.broadcastMessage("${ChatColor.GREEN}Starting ingame phase.")
                    UserList.clearAndCloseAllInvs()
                    startIngamePhase()
                    timeHeading = "Time:"
                }
                wholeDuration+1 -> {
                    Bukkit.broadcastMessage("${ChatColor.RED}Round stopping.")
                    startRoundTask()
                    timeHeading = "Starting in:"
                    time = 0
                }
            }
            when {
                counterLong < startDuration -> {
                    // In countdown (starting in ...)
                    time = 3L-counterLong
                }
                counterLong < ingameStart -> {
                    time = ingameStart-counterLong
                }
                counterLong <= wholeDuration -> {
                    time = wholeDuration-counterLong
                }
            }
            PLUGIN.updateScoreboards()
        }
    }

    open fun onNewStart() {}

    abstract fun getGameState(): GameState

    fun isPreparation(): Boolean = activePhase == Phase.PREPARATION
    fun isIngame(): Boolean = activePhase == Phase.INGAME

    abstract fun startPreparationPhase()
    abstract fun startIngamePhase()
    abstract fun getScoreboardHeading(): String
    abstract fun getScoreboardContent(): String

    fun stop() {
        unregister() // Unregister this as event listener
        currentTask?.cancel()
    }

    /** Get's called by subclass when a player has finished */
    fun finish(uuid: UUID) {
        finishedPlayers.add(uuid)
        // If all players have finished
        if (finishedPlayers.size == UserList.size) {
            finishedPlayers.clear()
            Bukkit.getLogger().info("Finishing because ${finishedPlayers.size} players have finished of a total of ${UserList.size} players.")
            Bukkit.getLogger().info("Starting round $roundCounter earlier")
            // Cancel current task
            currentTask?.cancel()

            // Clear invs
            UserList.clearAndCloseAllInvs()

            if (rounds-roundCounter == 0L) {
                // No more rounds
                finishPhase()
                return
            }
            Bukkit.getLogger().info("Setting howOften to ${rounds-roundCounter}")
            finishRound()
            PLUGIN.updateScoreboards()
        }
    }

    private fun finishPhase() {
        Bukkit.getLogger().info("No more rounds left. Calling next phase.")
        Bukkit.broadcastMessage("${ChatColor.YELLOW}Phase finished.")
        Bukkit.broadcastMessage("${ChatColor.GREEN}Next phase starting in 5 seconds.")
        timeHeading = "..."
        time  = 0
        PLUGIN.updateScoreboards()
        task(delay = 20L*5) {
            GamePhaseManager.nextPhase()
        }
    }

    private fun finishRound() {
        Bukkit.broadcastMessage("${ChatColor.YELLOW}Round finished.")
        timeHeading = ""
        PLUGIN.updateScoreboards()
        startRoundTask()
    }
}
