package de.hglabor.speedrun.scoreboard

import de.hglabor.utils.noriskutils.ChatUtils
import de.hglabor.utils.noriskutils.scoreboard.ScoreboardPlayer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Scoreboard
import java.util.*

open class SpeedrunScoreboardPlayer(protected val uuid: UUID) : ScoreboardPlayer {
    var mScoreboard: Scoreboard? = null
    var mObjective: Objective? = null
    override fun getScoreboard(): Scoreboard? = mScoreboard
    override fun setScoreboard(scoreboard: Scoreboard?) { mScoreboard = scoreboard }
    override fun getObjective(): Objective? = mObjective
    override fun setObjective(objective: Objective?) { mObjective = objective }
    override fun getLocale(): Locale = ChatUtils.locale(uuid)
    override fun getPlayer(): Player = Bukkit.getPlayer(uuid)!!
}