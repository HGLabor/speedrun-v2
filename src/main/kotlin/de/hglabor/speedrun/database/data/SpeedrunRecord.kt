package de.hglabor.speedrun.database.data

import kotlinx.serialization.Serializable

/**
 * @param name the name of the player
 * @param time the total time of the player
 */
@Serializable
data class SpeedrunRecord(val name: String, val time: Float)
