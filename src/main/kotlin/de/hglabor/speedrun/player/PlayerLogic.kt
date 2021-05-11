package de.hglabor.speedrun.player

import de.hglabor.speedrun.config.PREFIX
import de.hglabor.speedrun.game.GameState
import de.hglabor.speedrun.game.phase.GamePhaseManager
import de.hglabor.speedrun.utils.spectator
import net.axay.kspigot.extensions.bukkit.actionBar
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.util.*
import kotlin.math.roundToInt


class PlayerLogic {
    private val playerFinished: MutableMap<UUID, Boolean>
    private var finishedPlayers: MutableList<UUID>
    private fun isFinished(player: Player): Boolean {
        return playerFinished.getOrDefault(player.uniqueId, false)
    }

    private fun setFinished(player: Player) {
        playerFinished.replace(player.uniqueId, true)
    }

    fun addPlayer(player: Player) {
        if (playerFinished.containsKey(player.uniqueId)) {
            playerFinished.replace(player.uniqueId, false)
        } else {
            playerFinished[player.uniqueId] = false
        }
    }

    fun setTimeIfNotFinished() {
        if (GamePhaseManager.currentPhase.isIngame()) {
            val timeNeeded = System.currentTimeMillis()
            val elapsedTime: Long = timeNeeded - GamePhaseManager.currentPhase.startMillis!!
            for (uuid: UUID in playerFinished.keys) {
                if (!playerFinished[uuid]!!) {
                    val speedRunner: SpeedRunner = UserList[uuid]!!
                    speedRunner.timeNeeded = elapsedTime / 1000.0F
                    val player = Bukkit.getPlayer(uuid)
                    player?.playSound(player.location, Sound.ENTITY_PLAYER_HURT, 1F, 0F)
                }
            }
        }
    }

    fun sendElapsedTimeToEveryone() {
        UserList.players.forEach {
            if (isFinished(it)) {
                it.actionBar (
                    "§6Time needed: §e" + UserList.get(it)!!.timeNeeded.toString() + "s"
                )
            }
        }
    }

    fun finishPlayerOnce(player: Player) {
        if (!isFinished(player)) {
            setFinished(player)
            finishedPlayers.add(player.uniqueId)
            val speedRunner: SpeedRunner = UserList.get(player)!!
            val timeNeeded = System.currentTimeMillis()
            val elapsedTime: Long = timeNeeded - GamePhaseManager.currentPhase.startMillis!!
            speedRunner.timeNeeded = elapsedTime / 1000.0F
            player.actionBar("§6Time needed: §e" + speedRunner.timeNeeded.toString() + "s")
            player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1F, 0F)
            Bukkit.broadcastMessage(PREFIX + ChatColor.GOLD + (finishedPlayers.indexOf(player.uniqueId) + 1).toString() + "." + " " + ChatColor.AQUA.toString() + player.name)
            if (GamePhaseManager.currentState != GameState.Crafting) {
                player.spectator()
            }
            //HideUtils.showAllPlayer(player) //TODO create HideUtils
            checkIfEveryoneIsFinished()
        }
    }

    private fun checkIfEveryoneIsFinished() {
        if (GamePhaseManager.currentState == GameState.Crafting) {
            return
        }
        for (value: Boolean? in playerFinished.values) {
            if (!value!!) return
        }
        //GamePhaseManager.currentPhase.setTimer(GamePhaseManager.currentPhase.roundDuration - 1) //TODO nothing just remove this class its bad
    }

    fun addTimeToTotal() {
        UserList.forEach { entry -> entry.value.timeNeededTotal = entry.value.timeNeeded }
    }

    fun addEveryone() {
        finishedPlayers = ArrayList()
        UserList.players.forEach { player: Player -> addPlayer(player) }
    }

    fun sortToBestTime() {
        winnerList = ArrayList()
        UserList.entries.forEach { winnerList = winnerList + it.value }
        (winnerList as ArrayList<SpeedRunner>).sortWith { s1: SpeedRunner, s2: SpeedRunner -> ((s1.timeNeededTotal - s2.timeNeededTotal).roundToInt()) }
    }

    fun celebrateWinner() {
        val speedRunner: SpeedRunner = winner
        val player: Player = Bukkit.getPlayer(speedRunner.uuid)!!
        player.playSound(player.location, Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1F, 0F)
    }

    fun printTop10() {
        if (winnerList.size >= 10) {
            for (i in 0..9) {
                val speedRunner: SpeedRunner = winnerList[i]
                Bukkit.broadcastMessage(
                    PREFIX + ChatColor.GOLD.toString() + "" + (i + 1).toString() + "." + " " + ChatColor.AQUA + speedRunner.name +
                            ChatColor.GRAY.toString() + " | " + ChatColor.RED.toString() + "Total time: " + ChatColor.YELLOW.toString() + "" + String.format(
                        "%.3f",
                        speedRunner.timeNeededTotal
                    ) + "s"
                )
            }
        } else {
            for (i in winnerList.indices) {
                val speedRunner: SpeedRunner = winnerList[i]
                Bukkit.broadcastMessage(
                    (PREFIX + ChatColor.GOLD.toString() + "" + (i + 1).toString() + "." + " " + ChatColor.AQUA + speedRunner.name +
                            ChatColor.GRAY.toString() + " | " + ChatColor.RED.toString() + "Total time: " + ChatColor.YELLOW.toString() + "" + String.format(
                        "%.3f",
                        speedRunner.timeNeededTotal
                    ) + "s")
                )
            }
        }
    }

    companion object {
        private lateinit var winnerList: List<SpeedRunner>
        val winner: SpeedRunner
            get() = if (winnerList.isNotEmpty()) winnerList[0] else SpeedRunner()
    }

    init {
        playerFinished = HashMap()
        finishedPlayers = ArrayList()
    }
}