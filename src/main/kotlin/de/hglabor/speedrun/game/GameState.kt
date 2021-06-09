package de.hglabor.speedrun.game

import de.hglabor.speedrun.worlds.Worlds

enum class GameState {
    Lobby, Crafting, Portal, Stronghold, Crystal, Win
    ;

    val world by lazy { Worlds[name]!! }
}