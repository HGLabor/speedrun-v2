package de.hglabor.speedrun.command

import de.hglabor.speedrun.game.phase.GamePhaseManager
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class NextPhaseCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, p1: Command, p2: String, p3: Array<out String>): Boolean {
        if (sender is Player) {
            sender.sendMessage("${ChatColor.YELLOW}Skipping round.")
            GamePhaseManager.nextPhase()
            return true
        }
        return false
    }
}