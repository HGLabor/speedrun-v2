package de.hglabor.speedrun.worlds

import de.hglabor.speedrun.utils.addY
import net.axay.kspigot.extensions.geometry.subtract
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.BlockFace
import kotlin.math.cos
import kotlin.math.sin

var CRAFTING_SPAWNS: MutableList<Location>? = null; private set

fun createWorldStructures() {
    crafting(Worlds["crafting"]!!)
}

private fun crafting(craftingWorld: World) {
    val loc = craftingWorld.spawnLocation.clone().subtract(0, 1, 0)
    cylinder(loc, Material.STRIPPED_OAK_WOOD, 30)
    getCircle(loc.addY(1), 20.0, 20).forEach { it.block.type = Material.CRAFTING_TABLE }
    CRAFTING_SPAWNS = getCircle(loc.addY(1), 16.0, 20)
    CRAFTING_SPAWNS!!.forEach { it.block.getRelative(BlockFace.DOWN).type = Material.BEDROCK }
}

// Definitely not stolen
fun getCircle(center: Location, radius: Double, amount: Int): MutableList<Location> {
    val locations: MutableList<Location> = ArrayList()
    val world = center.world
    val increment = 2 * Math.PI / amount
    for (i in 0 until amount) {
        val angle = i * increment
        val x = center.x + radius * cos(angle)
        val z = center.z + radius * sin(angle)
        locations.add(Location(world, x, center.y, z))
    }
    return locations
}

// Definitely not stolen as well
fun cylinder(loc: Location, mat: Material?, r: Int) {
    val cx = loc.blockX
    val cy = loc.blockY
    val cz = loc.blockZ
    val w = loc.world
    val rSquared = r * r
    for (x in cx - r..cx + r) {
        for (z in cz - r..cz + r) {
            if ((cx - x) * (cx - x) + (cz - z) * (cz - z) <= rSquared) {
                w!!.getBlockAt(x, cy, z).type = mat!!
            }
        }
    }
}