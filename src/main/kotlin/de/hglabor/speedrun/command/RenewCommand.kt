package de.hglabor.speedrun.command

import de.hglabor.speedrun.config.PREFIX
import de.hglabor.speedrun.game.phase.GamePhaseManager
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class RenewCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, p1: Command, p2: String, p3: Array<out String>): Boolean {
        if (sender is Player) {
            if (GamePhaseManager.currentPhase.onRenew(sender)) sender.sendMessage("$PREFIX ${ChatColor.GREEN}Renew successful")
            return true
        }
        return false
    }
}