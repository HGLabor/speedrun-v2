package de.hglabor.speedrun.player

import de.hglabor.speedrun.scoreboard.SpeedrunScoreboardPlayer
import org.bukkit.Bukkit
import java.util.*

class SpeedRunner(uuid: UUID) : SpeedrunScoreboardPlayer(uuid) {
    val name: String? = Bukkit.getOfflinePlayer(uuid).name
    var timeNeededTotal: Float = 0.0F
    private var status: Status? = null

    fun addTotalTime(time: Float) { timeNeededTotal += time }

    enum class Status {
        PLAYER, SPECTATOR
    }

    fun scoreboardNull(): Boolean = mScoreboard == null

    init {
        status = Status.PLAYER
    }
}
