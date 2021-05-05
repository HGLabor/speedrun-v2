package de.hglabor.speedrun

import net.axay.kspigot.main.KSpigot

val PLUGIN by lazy { Speedrun.INSTANCE }

class Speedrun : KSpigot() {
    companion object {
        lateinit var INSTANCE: Speedrun; private set
    }

    override fun load() {
        INSTANCE = this
    }

    override fun startup() {
        super.startup()
    }

    override fun shutdown() {
        super.shutdown()
    }
}


