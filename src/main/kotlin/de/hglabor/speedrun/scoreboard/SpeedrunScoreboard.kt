package de.hglabor.speedrun.scoreboard

import de.hglabor.speedrun.config.Config
import de.hglabor.speedrun.config.MAX_PLAYERS
import de.hglabor.speedrun.game.GameState
import de.hglabor.speedrun.game.phase.GamePhaseManager
import de.hglabor.speedrun.player.SpeedRunner
import de.hglabor.speedrun.player.UserList
import de.hglabor.speedrun.utils.col
import de.hglabor.utils.noriskutils.scoreboard.ScoreboardFactory
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import java.util.concurrent.TimeUnit

object SpeedrunScoreboard {
    private const val disciplineHeadingName = "d_heading"
    private const val disciplineName = "discipline"

    // Each phase can customize this somehow lol
    private const val phaseHeadingName = "p_heading"
    private const val phaseName = "phase"

    private const val timeHeadingName = "t_heading"
    private const val timeName = "time"

    fun create(player: SpeedRunner) {
        ScoreboardFactory.create(player);
        ScoreboardFactory.addEntry(player, "9", "", 9)
        ScoreboardFactory.addEntry(player, disciplineHeadingName, "Discipline:", 8)
        ScoreboardFactory.addEntry(player, disciplineName, "", 7)
        ScoreboardFactory.addEntry(player, "6", "", 6)
        ScoreboardFactory.addEntry(player, phaseHeadingName, "PhaseCustomHeading:", 5)
        ScoreboardFactory.addEntry(player, phaseName, "PhaseCustom", 4)
        ScoreboardFactory.addEntry(player, "3", "", 3)
        ScoreboardFactory.addEntry(player, timeHeadingName, "Time:", 2)
        ScoreboardFactory.addEntry(player, timeName, "00:00", 1)
        ScoreboardFactory.addEntry(player, "0", "", 0)
    }

    fun update(player: SpeedRunner) {
        if (player.scoreboardNull()) {
            Bukkit.getLogger().warning("Scoreboard not yet created for player ${player.name}. Creating new one.");
            create(player);
        }
        // Change it to
        // Players:
        // AMOUNT/Config.MIN_PLAYERS.get() (max: MAX_PLAYERS)
        // when in lobby phase
        when (GamePhaseManager.currentState) {
            GameState.Lobby -> {
                val players = UserList.size
                ScoreboardFactory.updateEntry(player, disciplineHeadingName, "Players:")
                ScoreboardFactory.updateEntry(player, disciplineName, "${UserList.size}/${Config.MIN_PLAYERS.get()}".col(if(players>=Config.MIN_PLAYERS.get()) "green" else "yellow") + " (max ${MAX_PLAYERS})")
            }
            GameState.Win -> {
                ScoreboardFactory.updateEntry(player, disciplineHeadingName, "${ChatColor.GOLD}GAME END")
                ScoreboardFactory.updateEntry(player, disciplineName, "")
            }
            else -> {
                ScoreboardFactory.updateEntry(player, disciplineHeadingName, "Discipline:")
                ScoreboardFactory.updateEntry(player, disciplineName, ChatColor.YELLOW.toString() + GamePhaseManager.currentState.name)
            }
        }
        ScoreboardFactory.updateEntry(player, phaseHeadingName, GamePhaseManager.currentPhase.getScoreboardHeading())
        ScoreboardFactory.updateEntry(player, phaseName, GamePhaseManager.currentPhase.getScoreboardContent())
        ScoreboardFactory.updateEntry(player, timeHeadingName, GamePhaseManager.currentPhase.timeHeading)
        ScoreboardFactory.updateEntry(player, timeName, "${ChatColor.YELLOW}${formatTime(GamePhaseManager.currentPhase.time)}")
    }

    private fun formatTime(seconds: Long) = String.format("%02d:%02d", TimeUnit.SECONDS.toMinutes(seconds), TimeUnit.SECONDS.toSeconds(seconds))
}