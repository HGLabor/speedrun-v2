package de.hglabor.speedrun.worlds.generator

import org.bukkit.HeightMap
import org.bukkit.Material
import org.bukkit.generator.ChunkGenerator
import org.bukkit.generator.WorldInfo
import java.util.*

private const val CHUNK_SIZE = 16
private const val TERRAIN_HEIGHT = 66

class StrongholdGenerator : ChunkGenerator() {
    override fun generateNoise(worldInfo: WorldInfo, random: Random, x: Int, z: Int, chunk: ChunkData) {
        chunk.setRegion(0, chunk.minHeight, 0, CHUNK_SIZE, TERRAIN_HEIGHT, CHUNK_SIZE, Material.DIAMOND_BLOCK)
    }

    override fun getBaseHeight(worldInfo: WorldInfo, random: Random, x: Int, z: Int, heightMap: HeightMap) = TERRAIN_HEIGHT
    override fun shouldGenerateStructures() = true
    override fun shouldGenerateDecorations() = true
}