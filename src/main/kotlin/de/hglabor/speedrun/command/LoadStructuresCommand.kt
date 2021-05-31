package de.hglabor.speedrun.command

import de.hglabor.speedrun.config.PREFIX
import de.hglabor.speedrun.worlds.structures
import org.bukkit.ChatColor
import org.bukkit.command.*

class LoadStructuresCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, p1: Command, p2: String, p3: Array<out String>): Boolean {
        sender.sendMessage("$PREFIX ${ChatColor.DARK_AQUA}Loading...")
        structures()
        return true
    }
}