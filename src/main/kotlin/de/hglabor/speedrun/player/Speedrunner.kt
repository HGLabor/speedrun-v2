package de.hglabor.speedrun.player

import de.hglabor.utils.noriskutils.ChatUtils
import de.hglabor.utils.noriskutils.scoreboard.ScoreboardPlayer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Scoreboard
import java.util.*

class SpeedRunner : ScoreboardPlayer {
    val uuid: UUID
    val name: String?
    var timeNeededTotal = 0.0
        set(add) { timeNeededTotal += add }
    var timeNeeded = 0.0
    private var status: Status? = null
    private var mScoreboard: Scoreboard? = null
    private var mObjective: Objective? = null

    enum class Status {
        PLAYER, SPECTATOR
    }

    constructor(uuid: UUID) {
        this.uuid = uuid
        name = Bukkit.getOfflinePlayer(uuid).name
        status = Status.PLAYER
    }

    constructor(player: Player) {
        uuid = player.uniqueId
        name = player.name
        status = Status.PLAYER
    }

    constructor() {
        uuid = UUID.randomUUID()
        name = "Dummy"
    }

    override fun getScoreboard(): Scoreboard? = mScoreboard

    fun scoreboardNull(): Boolean = mScoreboard == null

    override fun setScoreboard(scoreboard: Scoreboard?) {
        mScoreboard = scoreboard
    }

    override fun getObjective(): Objective? = mObjective

    override fun setObjective(objective: Objective?) {
        mObjective = objective
    }

    override fun getLocale(): Locale = ChatUtils.locale(uuid)
    override fun getPlayer(): Player = Bukkit.getPlayer(uuid)!!
}
