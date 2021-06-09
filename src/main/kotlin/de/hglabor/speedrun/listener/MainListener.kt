package de.hglabor.speedrun.listener

import de.hglabor.speedrun.game.GameState
import de.hglabor.speedrun.game.phase.GamePhaseManager
import de.hglabor.speedrun.utils.cancel
import de.hglabor.speedrun.utils.isCreative
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.bukkit.feed
import org.bukkit.entity.*
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.*
import org.bukkit.event.player.*

fun mainListener() {
    listen<FoodLevelChangeEvent> {
        if (it.entity is Player) {
            (it.entity as Player).feed()
            it.cancel()
        }
    }
    listen<EntityDamageEvent> {
        if (it.entity is Player) it.cancel()
    }
    listen<BlockBreakEvent> {
        if (!it.player.isCreative() && !GamePhaseManager.currentPhase.buildingAllowed()) it.cancel()
    }
    listen<BlockPlaceEvent> {
        if (!it.player.isCreative() && !GamePhaseManager.currentPhase.buildingAllowed()) it.cancel()
    }
    listen<PlayerDropItemEvent> {
        if (!it.player.isCreative()) it.cancel()
    }
    listen<PlayerMoveEvent> {
        if (it.player.location.y <= 10 && GamePhaseManager.currentState != GameState.Stronghold) it.player.teleport(it.player.world.spawnLocation)
    }
    listen<PlayerInteractAtEntityEvent> {
        if (!it.player.isCreative() && it.rightClicked is ArmorStand) it.cancel()
    }
    listen<EntityDamageByEntityEvent> {
        if (it.damager is Player && !it.damager.isCreative()) it.cancel()
    }

    listen<EntitySpawnEvent> {
        if (it.entityType == EntityType.ENDER_DRAGON) it.cancel()
    }
}