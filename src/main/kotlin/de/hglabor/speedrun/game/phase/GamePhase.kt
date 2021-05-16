package de.hglabor.speedrun.game.phase

import de.hglabor.speedrun.PLUGIN
import de.hglabor.speedrun.config.PREFIX
import de.hglabor.speedrun.game.GameState
import de.hglabor.speedrun.player.UserList
import de.hglabor.speedrun.utils.*
import de.hglabor.utils.noriskutils.SoundUtils
import net.axay.kspigot.event.register
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.extensions.bukkit.actionBar
import net.axay.kspigot.runnables.KSpigotRunnable
import net.axay.kspigot.runnables.task
import net.axay.kspigot.runnables.taskRunLater
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Sound
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import java.util.*

abstract class GamePhase(private var rounds: Int = 1, private var preparationDuration: Int, private var roundDuration: Int) : Listener {
    private var activePhase = Phase.PREPARATION
    private var currentTask: KSpigotRunnable? = null
    private var finishedPlayers = ArrayList<UUID>()
    var timeHeading: String = "Starting in:"
    var time: Long = 0L
    var roundNumber = 0
    var startMillis: Long? = null

    private val startDuration: Long = 3 // 3 seconds

    enum class Phase {
        PREPARATION, INGAME
    }

    init {
        register() // Register this as event listener

        // Display title
        UserList.players.forEach { player ->
            player.sendTitle(getGameState().name.col("red"), "", 5, 10, 5)
        }
        // Teleport players delayed and set gamemode to survival
        taskRunLater(20L) {
            UserList.players.forEach { player ->
                SoundUtils.playTeleportSound(player)
                player.survival()
            }
            teleportPlayers()
        }
        if (roundDuration != -1) {
            startRoundTask()
        }
    }

    open fun teleportPlayers() = UserList.players.forEach { it.teleportToWorld(getGameState().name) }

    open fun buildingAllowed() = false

    open fun onRenew(player: Player): Boolean = false

    private fun isFinished() = rounds != -1 && roundNumber >= rounds

    private fun startRoundTask() {
        roundNumber++
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
                    teleportPlayers()
                    UserList.players.forEach { player ->
                        player.noMove(3)
                        player.survival()
                    }
                    timeHeading = "Starting in:"
                }
                startDuration -> {
                    broadcastLine()
                    grayBroadcast("$PREFIX Round ${roundNumber.toString().col("bold", "aqua")}")
                    UserList.players.forEach { player -> player.playSound(player.location, Sound.ENTITY_EVOKER_CAST_SPELL, 1F, 0F) }
                    UserList.clearAndCloseAllInvs()
                    activePhase = Phase.PREPARATION
                    startPreparationPhase()
                    broadcastRoundInfo()
                    broadcastLine()
                    timeHeading = "Preparation Time:"
                }
                ingameStart -> {
                    UserList.clearAndCloseAllInvs()
                    UserList.players.forEach { player -> player.playSound(player.location, Sound.BLOCK_BEEHIVE_ENTER, 1F, 0F) }
                    activePhase = Phase.INGAME
                    startIngamePhase()
                    timeHeading = "Time:"
                    startMillis = System.currentTimeMillis()
                }
                wholeDuration+1 -> {
                    activePhase = Phase.PREPARATION
                    onStop()
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
    open fun broadcastRoundInfo() {}

    private fun onStop() {
        if (isFinished()) {
            finishRound()
            currentTask = finishPhaseDelayed()
        }
        else {
            timeHeading = "Starting in:"
            time = 0
            startRoundTask()
        }
    }

    abstract fun getGameState(): GameState

    fun isIngame(): Boolean = activePhase == Phase.INGAME

    abstract fun startPreparationPhase()
    abstract fun startIngamePhase()
    abstract fun getScoreboardHeading(): String
    abstract fun getScoreboardContent(): String

    open fun stop() {
        unregister() // Unregister this as event listener
        currentTask?.cancel()
    }

    /** Get's called by subclass when a player has finished */
    fun finish(uuid: UUID) {
        if (finishedPlayers.contains(uuid)) return
        finishedPlayers.add(uuid)
        val player = Bukkit.getPlayer(uuid)!!
        Bukkit.broadcastMessage("$PREFIX ${ChatColor.GOLD}${finishedPlayers.size}. ${ChatColor.AQUA}${player.displayName}")
        player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1F, 0F)
        if (startMillis != null) {
            val elapsedTime = (System.currentTimeMillis() - startMillis!!) / 1000F
            player.actionBar("§6Time needed: §e" + elapsedTime + "s")
            UserList[player.uniqueId]!!.addTotalTime(elapsedTime)
        }
        player.spectator()
        // If all players have finished
        if (finishedPlayers.size == UserList.size) {
            Bukkit.getLogger().info("Finishing because ${finishedPlayers.size} players have finished of a total of ${UserList.size} players.")
            broadcast("$PREFIX ${ChatColor.GREEN}All players have finished.")
            Bukkit.getLogger().info("Starting round $roundNumber earlier")
            // Cancel current task
            currentTask?.cancel()

            // Clear invs
            UserList.clearAndCloseAllInvs()
            finishRound()
            finishedPlayers.clear()
            if (isFinished()) {
                // No more rounds
                currentTask = finishPhaseDelayed()
                return
            }
            currentTask = startRoundTaskDelayed()
        }
    }

    /**
     * @param delay the delay in seconds
     */
    private fun startRoundTaskDelayed(delay: Long = 3): KSpigotRunnable = task(howOften = delay+1, period = 20L) {
            timeHeading = "Next Round:"
            time = delay-it.counterUp!!+1
            PLUGIN.updateScoreboards()
            if (it.counterUp == delay+1) {
                startRoundTask()
            }
        }!!

    /**
     * @param delay the delay in seconds
     */
    private fun finishPhaseDelayed(delay: Long = 5) = task(howOften = delay+1, period = 20L) {
            timeHeading = "Next Discipline:"
            time = delay-it.counterUp!!+1
            PLUGIN.updateScoreboards()
            if (it.counterUp == delay+1) {
                finishPhase()
            }
        }!!

    private fun finishPhase() {
        GamePhaseManager.nextPhase()
    }

    private fun finishRound() {
        timeHeading = ""
        time  = 0
        PLUGIN.updateScoreboards()
        val totalRoundTime = (System.currentTimeMillis() - startMillis!!) / 1000F
        UserList.forEach {
            if (!finishedPlayers.contains(it.key)) {
                // Player has not finished in time. Add the total time to the player
                it.value.addTotalTime(totalRoundTime)
                // Set player's gamemode to spectator
                it.value.player.spectator()
            }
        }
    }

    fun ingameNotFinished(player: HumanEntity): Boolean = player.gameMode != GameMode.SPECTATOR && isIngame()
}
