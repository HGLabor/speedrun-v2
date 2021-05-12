package de.hglabor.speedrun.game.phase.phases

import de.hglabor.speedrun.PLUGIN
import de.hglabor.speedrun.game.GameState
import de.hglabor.speedrun.game.phase.GamePhase
import de.hglabor.speedrun.player.UserList
import de.hglabor.speedrun.utils.spectator
import de.hglabor.speedrun.worlds.PORTAL_SPAWNS
import de.hglabor.utils.noriskutils.SoundUtils
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent

class PortalPhase : GamePhase(0, -1, -1) {
    override fun startPreparationPhase() {}
    override fun startIngamePhase() {}
    override fun getScoreboardHeading(): String = "Leave"
    override fun getScoreboardContent(): String = "${ChatColor.RED}/hub"

    override fun getGameState(): GameState = GameState.Portal

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) { PLUGIN.updateScoreboards() }

    override fun teleportPlayers() {
        val locations = PORTAL_SPAWNS!!.shuffled()
        UserList.players.forEachIndexed { index, player ->
            val loc = locations[index]
            loc.x += 0.5
            loc.y = 20.0
            loc.z += 0.5
            player.teleport(loc)
            player.spectator()
            SoundUtils.playTeleportSound(player)
        }
    }
}