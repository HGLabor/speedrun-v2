package de.hglabor.speedrun.command

import de.hglabor.speedrun.config.PREFIX
import de.hglabor.speedrun.game.phase.GamePhaseManager
import de.hglabor.speedrun.utils.grayBroadcast
import net.axay.kspigot.chat.KColors
import org.bukkit.command.*
import org.bukkit.entity.Player

object StartCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, p1: Command, p2: String, p3: Array<out String>): Boolean {
        if (sender is Player && GamePhaseManager.currentPhase.onStart(sender)) {
            grayBroadcast("$PREFIX ${KColors.YELLOW}Force started.")
            return true
        }
        return false
    }
}