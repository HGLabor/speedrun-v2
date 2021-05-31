package de.hglabor.speedrun.command

import de.hglabor.speedrun.config.Config
import de.hglabor.speedrun.config.PREFIX
import org.bukkit.ChatColor
import org.bukkit.command.*

class ReloadCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, p1: Command, p2: String, p3: Array<out String>): Boolean {
        Config.reload()
        sender.sendMessage("$PREFIX ${ChatColor.DARK_AQUA}Reloaded config.")
        return true
    }
}