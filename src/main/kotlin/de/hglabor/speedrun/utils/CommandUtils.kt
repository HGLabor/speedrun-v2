package de.hglabor.speedrun.utils

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

fun command(command: String, sender: CommandSender = Bukkit.getConsoleSender()) = Bukkit.dispatchCommand(sender, command)