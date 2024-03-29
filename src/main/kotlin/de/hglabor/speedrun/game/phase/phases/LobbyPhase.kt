package de.hglabor.speedrun.game.phase.phases

import de.dytanic.cloudnet.ext.bridge.bukkit.BukkitCloudNetHelper
import de.hglabor.speedrun.PLUGIN
import de.hglabor.speedrun.config.Config
import de.hglabor.speedrun.config.PREFIX
import de.hglabor.speedrun.database.SpeedrunDB
import de.hglabor.speedrun.database.data.locations
import de.hglabor.speedrun.game.GameState
import de.hglabor.speedrun.game.phase.GamePhase
import de.hglabor.speedrun.game.phase.GamePhaseManager
import de.hglabor.speedrun.player.SpeedRunner
import de.hglabor.speedrun.player.UserList
import de.hglabor.utils.kutils.*
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.runnables.KSpigotRunnable
import net.axay.kspigot.runnables.task
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.litote.kmongo.findOne

class LobbyPhase : GamePhase(0, -1, -1) {
    init {
        updateHolograms()
    }
    private var lifetimeRecordHologram: Hologram? = null
    fun updateHolograms() {
        lifetimeRecordHologram?.remove()
        val record = SpeedrunDB.recordsCollection.findOne()
        val location = locations.lifetimeRecordLocation
        lifetimeRecordHologram = if (record != null) hologram(location,
            "${KColors.GOLDENROD}Lifetime Record: ${KColors.SILVER}${record.time}s ${KColors.GOLDENROD}by ${KColors.SILVER}${record.name}")
        else hologram(location,
            "${KColors.GOLDENROD}Lifetime Record: Not available")
    }

    override fun startPreparationPhase() {}
    override fun startIngamePhase() {}
    override fun getScoreboardHeading(): String = "Leave"
    override fun getScoreboardContent(player: SpeedRunner): String = "${ChatColor.RED}/hub"

    override fun hasLeaveItem() = true

    private var task: KSpigotRunnable? = null

    private var startingIn = -1
    set(value) {
        field = value
        when(value) {
            -1 -> {
                broadcast("$PREFIX ${KColors.ORANGERED}Not enough players (${UserList.size}). Start cancelled")
                task?.cancel()
            }
            120, 90, 60, 30, 20 -> announceTime()
            10 -> {
                announceTime()
                cloudNet {
                    BukkitCloudNetHelper.setMotd("Starting...")
                    BukkitCloudNetHelper.setState("STARTING")
                }
            }

        }
        time = startingIn.toLong()
        PLUGIN.updateScoreboards()
    }
    private val hasStarted get() = startingIn != -1

    private fun announceTime() {
        if (startingIn != 0) broadcast("$PREFIX ${KColors.GREENYELLOW}Starting in ${KColors.GREEN}$startingIn ${KColors.GREENYELLOW} seconds.")
        else broadcast("$PREFIX ${KColors.GREEN}Starting...")
    }

    override fun state() = GameState.Lobby
    override fun stop() {
        super.stop()
        task?.cancel()
        cloudNet {
            BukkitCloudNetHelper.changeToIngame()
        }
    }

    override fun onStart(player: Player): Boolean {
        val time = Config.START_TIME_START_COMMAND.getInt()
        return if (startingIn <= time) false
        else {
            startingIn = time
            if (!hasStarted) {
                player.sendMessage("${KColors.RED}Not enough players.")
                return false
            }
            true
        }
    }

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
        if (hasStarted && UserList.size < Config.MIN_PLAYERS.getInt()) {
            if (startingIn <= 10) {
                cloudNet {
                    BukkitCloudNetHelper.setMotd("Waiting for players...")
                    BukkitCloudNetHelper.setState("LOBBY")
                }
            }
            startingIn = -1
        }
    }

    override fun onRenew(player: Player): Boolean {
        player.tpSpawn()
        return true
    }
}