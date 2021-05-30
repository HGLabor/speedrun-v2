package de.hglabor.speedrun.worlds

import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.session.ClipboardHolder
import de.hglabor.speedrun.PLUGIN
import de.hglabor.speedrun.utils.addY
import net.axay.kspigot.extensions.geometry.add
import net.axay.kspigot.extensions.geometry.subtract
import org.bukkit.*
import org.bukkit.block.BlockFace
import java.io.File
import java.io.FileInputStream
import kotlin.math.cos
import kotlin.math.sin

var CRAFTING_SPAWNS: MutableList<Location>? = null; private set
var PORTAL_SPAWNS: MutableList<Location>? = null
val LAVA_ARENA_FILE = File(PLUGIN.dataFolder.absolutePath + "/lavaArena_02.schem")
const val LAVA_ARENA_WIDTH = 10
const val ARENA_COUNT = 20

fun structures() {
    craftingStructures(Worlds["crafting"]!!)
    portalStructures(Worlds["portal"]!!)
}

// Crafting

fun craftingStructures(craftingWorld: World) {
    val loc = craftingWorld.spawnLocation.clone().subtract(0, 1, 0)
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


// Portal

fun portalStructures(portalWorld: World) {
    portalWorld.loadChunk(0, 0)
    val startLocation = Location(portalWorld, 0.0, 20.0, 0.0)
    val clipboard = getPortalClipboard()
    if (clipboard != null) pastePortals(startLocation, clipboard)
}

fun pastePortals(location: Location, clipboard: Clipboard) {
    PORTAL_SPAWNS = ArrayList()
    for (i in 0 until ARENA_COUNT) {
        val newLoc = location.clone().add((LAVA_ARENA_WIDTH+5)*i, 0, 0)
        pastePortal(newLoc, clipboard)
        PORTAL_SPAWNS!!.add(newLoc)
    }
}

fun pastePortal(location: Location, clipboard: Clipboard) {
    val world = BukkitAdapter.adapt(location.world)
    val editSession = WorldEdit.getInstance().editSessionFactory.getEditSession(world, -1)
    val operation = ClipboardHolder(clipboard)
        .createPaste(editSession)
        .to(BlockVector3.at(location.x, location.y, location.z))
        .ignoreAirBlocks(false)
        .build()
    Operations.complete(operation)
    editSession.close()
}

fun getPortalClipboard(): Clipboard? {
    val format = ClipboardFormats.findByFile(LAVA_ARENA_FILE)
    if (format != null) {
        return format.getReader(FileInputStream(LAVA_ARENA_FILE)).read()
    }
    return null
}

fun requirePortalClipboard(): Clipboard = getPortalClipboard()!!