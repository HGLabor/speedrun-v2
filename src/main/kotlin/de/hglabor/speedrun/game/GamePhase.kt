package de.hglabor.speedrun.game

abstract class GamePhase {
    private lateinit var rounds: Number

    abstract fun startRound()
}
