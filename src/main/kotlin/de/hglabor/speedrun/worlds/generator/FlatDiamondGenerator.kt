package de.hglabor.speedrun.worlds.generator

import nl.rutgerkok.worldgeneratorapi.BaseChunkGenerator
import nl.rutgerkok.worldgeneratorapi.BaseChunkGenerator.CHUNK_SIZE
import nl.rutgerkok.worldgeneratorapi.BaseTerrainGenerator
import org.bukkit.Material

class FlatDiamondGenerator : BaseTerrainGenerator {
    override fun getHeight(x: Int, z: Int, type: BaseTerrainGenerator.HeightType): Int {
        return 63
    }

    override fun setBlocksInChunk(chunk: BaseChunkGenerator.GeneratingChunk) {
        chunk.blocksForChunk.setRegion(0, 0, 0, CHUNK_SIZE, 63, CHUNK_SIZE, Material.DIAMOND_BLOCK)
    }
}