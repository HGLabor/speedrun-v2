package de.hglabor.speedrun.game.phase.phases

import de.hglabor.speedrun.PLUGIN
import de.hglabor.speedrun.config.PREFIX
import de.hglabor.speedrun.game.GameState
import de.hglabor.speedrun.game.phase.GamePhase
import de.hglabor.speedrun.player.SpeedRunner
import de.hglabor.speedrun.player.UserList
import de.hglabor.speedrun.utils.broadcastLine
import de.hglabor.speedrun.utils.col
import net.axay.kspigot.extensions.broadcast
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent


class WinPhase : GamePhase(0, -1, -1) {
    private val winners = sortPlayersBestTime()

    init {
        broadcastLine()
        val magic = ("|".repeat(11)).col("white", "magic")
        broadcast("$PREFIX $magic ${ChatColor.GOLD}GAME ENDED $magic")
        broadcastLine()
        // Broadcast top 10 players
        if (winners.size >= 10) for (i in 0..9) { broadcastWinner(i) }
        else winners.indices.forEach { broadcastWinner(it) }
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

    override fun getGameState(): GameState = GameState.WIN

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) { PLUGIN.updateScoreboards() }

    private fun sortPlayersBestTime(): List<SpeedRunner> = UserList.values.sortedWith { s1, s2 -> (s1.timeNeededTotal - s2.timeNeededTotal).toInt() }
}