package de.hglabor.speedrun.game

import de.hglabor.speedrun.worlds.Worlds

enum class GameState {
    Lobby, Crafting, Portal, Blazes, Stronghold, Crystal, Win
    ;

    val world get() = Worlds[name]!!
}