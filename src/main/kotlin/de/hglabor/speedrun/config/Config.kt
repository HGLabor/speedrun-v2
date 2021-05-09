package de.hglabor.speedrun.config

import org.bukkit.ChatColor

object Config {
    fun getPrefix(): String = "${ChatColor.DARK_GRAY}[${ChatColor.AQUA}Speedrun${ChatColor.DARK_GRAY}]"
}