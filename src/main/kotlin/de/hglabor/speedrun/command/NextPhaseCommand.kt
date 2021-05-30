package de.hglabor.speedrun.command

import de.hglabor.speedrun.config.PREFIX
import de.hglabor.speedrun.game.phase.GamePhaseManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.*
import org.bukkit.entity.Player

class NextPhaseCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, p1: Command, p2: String, p3: Array<out String>): Boolean {
        if (sender is Player) {
            Bukkit.broadcastMessage("$PREFIX ${ChatColor.YELLOW}Skipping phase.")
            GamePhaseManager.nextPhase()
            return true
        }
        return false
    }
}