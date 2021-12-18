package de.hglabor.speedrun.command

import de.hglabor.speedrun.config.Config
import de.hglabor.speedrun.config.PREFIX
import de.hglabor.speedrun.game.phase.GamePhaseManager
import de.hglabor.speedrun.worlds.structures
import de.hglabor.utils.kutils.grayBroadcast
import de.hglabor.utils.kutils.playSound
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.commands.*
import net.axay.kspigot.extensions.broadcast
import org.bukkit.ChatColor
import org.bukkit.Sound

fun commands() {
    // Speedrun
    command("speedrun") {
        requiresPermission("speedrun.admin")
        literal("next") {
            runs {
                broadcast("$PREFIX ${ChatColor.YELLOW}Skipping phase")
                GamePhaseManager.nextPhase()
            }
        }

        literal("loadstructures") {
            runs {
                player.sendMessage("$PREFIX ${ChatColor.DARK_AQUA}Loading structures...")
                structures()
                player.sendMessage("$PREFIX ${ChatColor.DARK_AQUA}Done")
            }
        }

        literal("reload") {
            runs {
                Config.reload()
                player.sendMessage("$PREFIX ${ChatColor.DARK_AQUA}Reloaded config")
            }
        }
    }

    // Renew
    command("renew") {
        runs {
            if (GamePhaseManager.currentPhase.onRenew(player)) {
                player.sendMessage("$PREFIX ${ChatColor.GREEN}Renew successful")
                player.playSound(Sound.BLOCK_ANVIL_USE)
            }
        }
    }

    // Start
    command("start") {
        requiresPermission("speedrun.start")
        runs {
            if (GamePhaseManager.currentPhase.onStart(player)) grayBroadcast("$PREFIX ${KColors.YELLOW}Force started")
        }
    }
}