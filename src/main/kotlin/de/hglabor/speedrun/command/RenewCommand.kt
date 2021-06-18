package de.hglabor.speedrun.command

import de.hglabor.speedrun.config.PREFIX
import de.hglabor.speedrun.game.phase.GamePhaseManager
import de.hglabor.speedrun.utils.playSound
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.command.*
import org.bukkit.entity.Player

class RenewCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, p1: Command, p2: String, p3: Array<out String>): Boolean =
        if (sender is Player && GamePhaseManager.currentPhase.onRenew(sender)) {
            sender.sendMessage("$PREFIX ${ChatColor.GREEN}Renew successful")
            sender.playSound(Sound.BLOCK_ANVIL_USE)
            true
        }
        else false
}