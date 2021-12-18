package de.hglabor.speedrun.utils

import de.hglabor.speedrun.config.PREFIX
import de.hglabor.speedrun.player.UserList
import de.hglabor.speedrun.scoreboard.SpeedrunScoreboard
import de.hglabor.utils.kutils.grayBroadcast
import net.axay.kspigot.chat.KColors
import org.bukkit.entity.Player

fun broadcastLine(): Int = grayBroadcast("$PREFIX ${KColors.BOLD}${"―".repeat(20)}")

fun Player.createScoreboard() { SpeedrunScoreboard.create(UserList[this.uniqueId]!!) }
fun Player.updateScoreboard() { SpeedrunScoreboard.update(UserList[this.uniqueId]!!) }