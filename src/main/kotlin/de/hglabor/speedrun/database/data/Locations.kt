package de.hglabor.speedrun.database.data

import de.hglabor.utils.kutils.world
import kotlinx.serialization.Serializable
import net.axay.kspigot.serialization.LocationSerializer
import org.bukkit.Location

@Serializable
data class Locations(
    @Serializable(with = LocationSerializer::class)
    var lifetimeRecordLocation: Location = world("lobby")!!.spawnLocation
)

var locations = Locations()