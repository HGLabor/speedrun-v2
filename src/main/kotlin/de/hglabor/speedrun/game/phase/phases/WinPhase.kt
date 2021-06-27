package de.hglabor.speedrun.game.phase.phases

import de.hglabor.speedrun.PLUGIN
import de.hglabor.speedrun.config.Config
import de.hglabor.speedrun.config.PREFIX
import de.hglabor.speedrun.game.GameState
import de.hglabor.speedrun.game.phase.GamePhase
import de.hglabor.speedrun.player.SpeedRunner
import de.hglabor.speedrun.player.UserList
import de.hglabor.speedrun.utils.*
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.runnables.KSpigotRunnable
import net.axay.kspigot.runnables.task
import org.bukkit.ChatColor


class WinPhase : GamePhase(0, -1, -1) {
    private val winners = sortPlayersBestTime()
    private var winTask: KSpigotRunnable? = null

    init {
        tpPlayers()
        UserList.players.forEach { it.survival() }

        broadcastLine()
        val magic = ("|".repeat(11)).col("gray", "magic")
        broadcast("$PREFIX $magic ${ChatColor.GOLD}GAME ENDED $magic")
        broadcastLine()
        // Broadcast top 10 players
        if (winners.size >= Config.BROADCAST_WINNERS_AMOUNT.getInt()) for (i in 0 until Config.BROADCAST_WINNERS_AMOUNT.getInt()) broadcastWinner(i)
        else winners.indices.forEach { broadcastWinner(it) }

        if (Config.DO_RESTART.getBoolean()) {
            timeHeading = "Restarting in:"
            val seconds = Config.RESTART_TIME.getInt().toLong()
            winTask = task(howOften = seconds + 1, period = 20L) {
                time = seconds - it.counterUp!!.toLong()
                when (time) {
                    in 1..5 -> grayBroadcast("$PREFIX Server is restarting in ${time.toString().col("aqua")} seconds.")
                    0L -> {
                        timeHeading = "Restarting..."
                        grayBroadcast("$PREFIX ${ChatColor.DARK_AQUA}Restarting...")
                    }
                    -1L -> command("restart")
                }
                PLUGIN.updateScoreboards()
            }
        }
        else {
            timeHeading = "${ChatColor.RED}Auto-Restart is off"
            PLUGIN.updateScoreboards()
        }
    }

    override fun stop() {
        super.stop()
        winTask?.cancel()
    }

    private fun broadcastWinner(i: Int) {
        val speedRunner = winners[i]
        broadcast("$PREFIX ${ChatColor.GOLD}${i+1}. ${ChatColor.AQUA}${speedRunner.name} " +
                "${ChatColor.GRAY}| ${ChatColor.RED}Total Time: ${ChatColor.YELLOW}${String.format("%.3f", speedRunner.timeNeededTotal)}s")
    }

    override fun startPreparationPhase() {}
    override fun startIngamePhase() {}
    override fun getScoreboardHeading(): String = "Winner:"
    override fun getScoreboardContent(): String = ChatColor.AQUA.toString() + winners[0].player.displayName

    override fun state() = GameState.Win

    private fun sortPlayersBestTime(): List<SpeedRunner> = UserList.values.sortedWith { s1, s2 -> (s1.timeNeededTotal - s2.timeNeededTotal).toInt() }
}