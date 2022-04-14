package de.hglabor.speedrun.scoreboard

import de.hglabor.utils.kutils.player
import de.hglabor.utils.noriskutils.ChatUtils
import de.hglabor.utils.noriskutils.scoreboard.ScoreboardPlayer
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Scoreboard
import java.util.*

open class SpeedrunScoreboardPlayer(private val uuid: UUID) : ScoreboardPlayer {
    private var mScoreboard: Scoreboard? = null
    private var mObjective: Objective? = null
    override fun getScoreboard(): Scoreboard? = mScoreboard
    override fun setScoreboard(scoreboard: Scoreboard?) { mScoreboard = scoreboard }
    override fun getObjective(): Objective? = mObjective
    override fun setObjective(objective: Objective?) { mObjective = objective }
    override fun getLocale(): Locale = ChatUtils.locale(uuid)
    override fun getPlayer(): Player = player(uuid)!!
}