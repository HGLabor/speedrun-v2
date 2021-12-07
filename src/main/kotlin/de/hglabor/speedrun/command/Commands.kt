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
        requiresPermission("speedrun.admin")
        literal("next") {
            runs {
                Bukkit.broadcastMessage("$PREFIX ${ChatColor.YELLOW}Skipping phase")
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