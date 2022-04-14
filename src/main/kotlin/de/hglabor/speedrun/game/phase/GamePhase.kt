package de.hglabor.speedrun.game.phase

import de.dytanic.cloudnet.driver.CloudNetDriver
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager
import de.hglabor.speedrun.PLUGIN
import de.hglabor.speedrun.config.PREFIX
import de.hglabor.speedrun.game.GameState
import de.hglabor.speedrun.player.UserList
import de.hglabor.speedrun.utils.broadcastLine
import de.hglabor.speedrun.worlds.Worlds
import de.hglabor.utils.kutils.*
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.extensions.bukkit.actionBar
import net.axay.kspigot.extensions.bukkit.title
import net.axay.kspigot.extensions.server
import net.axay.kspigot.runnables.*
import org.bukkit.*
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import java.util.*

abstract class GamePhase(private var rounds: Int = 1, private var preparationDuration: Int, private var roundDuration: Int) : Listener {
    private var activePhase = Phase.PREPARATION
    private var currentTask: KSpigotRunnable? = null
    private var finishedPlayers = ArrayList<UUID>()
    var timeHeading = "Starting in:"
    var time = 0L
    set(value) {
        field = value
        formattedTime = time.formatAsTime()
    }
    var formattedTime: String = ""
    var roundNumber = 0
    private var startMillis: Long? = null
    val world by lazy { Worlds[state().name]!! }
    private val spawn by lazy { world.spawnLocation }

    private val startDuration = 3L // 3 seconds

    enum class Phase {
        PREPARATION, INGAME
    }

    init {
        @Suppress("LeakingThis")
        server.pluginManager.registerEvents(this, PLUGIN) // Register this as event listener

        // Display title
        UserList.players.forEach { player ->
            player.title(state().name.col("red"), "", 5, 10, 5)
        }
        // Teleport players delayed and set gamemode to survival
        taskRunLater(20L) {
            UserList.players.forEach { player ->
                player.playSound(Sound.ENTITY_SHULKER_TELEPORT)
                player.survival()
                if (hasLeaveItem()) player.inventory.setItem(8, leaveItem)
            }
            if (roundDuration != -1) startRoundTask()
        }
    }

    protected fun Player.tpSpawn() = this.teleport(this@GamePhase.spawn)
    open fun tpPlayers() = UserList.players.forEach { it.tpSpawn() }

    open fun buildingAllowed() = false
    open fun hasLeaveItem() = false

    open fun onRenew(player: Player): Boolean = false
    open fun onStart(player: Player): Boolean = false
    open fun onFall(player: Player) { player.tpSpawn() }

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
            timeHeading = when(counterLong) {
                1L -> {
                    UserList.clearAndCloseAllInvs()
                    // Clear finished players
                    finishedPlayers.clear()

                    onNewStart()
                    tpPlayers()
                    UserList.players.forEach { player ->
                        player.noMove(3)
                        player.survival()
                    }
                    "Starting in:"
                }
                startDuration -> {
                    broadcastLine()
                    grayBroadcast("$PREFIX Round ${roundNumber.toString().col("bold", "aqua")}")
                    UserList.players.forEach { player -> player.playSound(Sound.ENTITY_EVOKER_CAST_SPELL, pitch = 0) }
                    UserList.clearAndCloseAllInvs()
                    activePhase = Phase.PREPARATION
                    startPreparationPhase()
                    broadcastRoundInfo()
                    broadcastLine()
                    "Preparation Time:"
                }
                ingameStart -> {
                    UserList.clearAndCloseAllInvs()
                    UserList.players.forEach { player -> player.playSound(Sound.BLOCK_BEEHIVE_ENTER, pitch = 0) }
                    activePhase = Phase.INGAME
                    startIngamePhase()
                    startMillis = System.currentTimeMillis()
                    "Time:"
                }
                wholeDuration+1 -> {
                    activePhase = Phase.PREPARATION
                    onStop()
                    ""
                }
                else -> timeHeading
            }
            // schon schön ngl
            time = when {
                counterLong < startDuration -> 3L-counterLong
                counterLong < ingameStart -> ingameStart-counterLong
                counterLong <= wholeDuration -> wholeDuration-counterLong
                else -> 0
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

    abstract fun state(): GameState

    abstract fun startPreparationPhase()
    abstract fun startIngamePhase()
    abstract fun getScoreboardHeading(): String
    abstract fun getScoreboardContent(): String

    open fun stop() {
        unregister() // Unregister this as event listener
        currentTask?.cancel()
    }

    /** Gets called by subclass when a player has finished */
    fun finish(uuid: UUID) = with(player(uuid)!!) {
        // Do everything with the player context
        if (finishedPlayers.contains(uuid)) return
        finishedPlayers.add(uuid)
        @Suppress("DEPRECATION")
        broadcast("$PREFIX ${ChatColor.GOLD}${finishedPlayers.size}. ${ChatColor.AQUA}${displayName}")
        playSound(Sound.ENTITY_PLAYER_LEVELUP, pitch = 0)
        if (startMillis != null) {
            val elapsedTime = (System.currentTimeMillis() - startMillis!!) / 1000F
            actionBar("§6Time needed: §e" + elapsedTime + "s")
            UserList[uniqueId]!!.addTotalTime(elapsedTime)
        }
        spectator()
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

    private fun finishPhase() { GamePhaseManager.nextPhase() }

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

    fun ingameNotFinished(player: HumanEntity): Boolean = player.gameMode != GameMode.SPECTATOR && activePhase == Phase.INGAME


    // Leave item

    private val leaveItem = namedItem(Material.RED_BED, "${KColors.RED}${KColors.BOLD}Leave")

    @EventHandler
    fun handlePlayerJoinForLeaveItem(event: PlayerJoinEvent) {
        if (hasLeaveItem()) event.player.inventory.setItem(8, leaveItem)
    }

    private val playerManager = CloudNetDriver.getInstance().servicesRegistry.getFirstService(IPlayerManager::class.java)

    @EventHandler
    fun handleInteractForLeaveItem(event: PlayerInteractEvent) {
        if (hasLeaveItem() && event.item?.isSimilar(leaveItem) == true) {
            event.cancel()
            playerManager.getPlayerExecutor(event.player.uniqueId).connectToFallback()
        }
    }

    @EventHandler
    fun handleInventoryClickForLeaveItem(event: InventoryClickEvent) {
        if (hasLeaveItem() && event.currentItem?.isSimilar(leaveItem) == true) {
            event.cancel()
            playerManager.getPlayerExecutor(event.whoClicked.uniqueId).connectToFallback()
        }
    }
}
