package de.hglabor.speedrun.command

import de.hglabor.speedrun.config.Config
import de.hglabor.speedrun.config.PREFIX
import de.hglabor.speedrun.game.phase.GamePhaseManager
import de.hglabor.speedrun.utils.grayBroadcast
import de.hglabor.speedrun.utils.playSound
import de.hglabor.speedrun.worlds.structures
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.commands.*
import org.bukkit.*

fun commands() {
    // Speedrun
    command("speedrun") {
        literal("next") {
            simpleExecutes {
                Bukkit.broadcastMessage("$PREFIX ${ChatColor.YELLOW}Skipping phase")
                GamePhaseManager.nextPhase()
            }
        }

        literal("loadstructures") {
            simpleExecutes {
                it.source.player.sendMessage("$PREFIX ${ChatColor.DARK_AQUA}Loading structures...")
                structures()
                it.source.player.sendMessage("$PREFIX ${ChatColor.DARK_AQUA}Done")
            }
        }

        literal("reload") {
            simpleExecutes {
                Config.reload()
                it.source.player.sendMessage("$PREFIX ${ChatColor.DARK_AQUA}Reloaded config")
            }
        }
    }

    // Renew
    command("renew") {
        simpleExecutes {
            if (GamePhaseManager.currentPhase.onRenew(it.source.player)) {
                it.source.player.sendMessage("$PREFIX ${ChatColor.GREEN}Renew successful")
                it.source.player.playSound(Sound.BLOCK_ANVIL_USE)
            }
        }
    }

    // Start
    command("start") {
        requires { it.player.hasPermission("speedrun.command.start") }
        simpleExecutes {
            if (GamePhaseManager.currentPhase.onStart(it.source.player)) grayBroadcast("$PREFIX ${KColors.YELLOW}Force started")
        }
    }
}