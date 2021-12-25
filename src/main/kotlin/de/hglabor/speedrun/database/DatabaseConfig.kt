package de.hglabor.speedrun.database

import de.hglabor.speedrun.PLUGIN
import org.bukkit.configuration.file.YamlConfiguration
import java.nio.file.Paths

object DatabaseConfig {
    private var file = Paths.get(PLUGIN.dataFolder.parentFile.toString(), "mongodb", "MongoDB.yml").toFile()
    private var yamlConfiguration = YamlConfiguration.loadConfiguration(file)

    lateinit var host: String
    var port = 0
    lateinit var username: String
    lateinit var password: String
    lateinit var database: String

    fun loadConfig() {
        host = yamlConfiguration["host"].toString()
        username = yamlConfiguration["username"].toString()
        password = yamlConfiguration["password"].toString()
        database = yamlConfiguration["database"].toString()
        port = yamlConfiguration["port"] as Int
        //Percent encoding oder sowas
        password = password.replace("%(?![0-9a-fA-F]{2})".toRegex(), "%25")
    }

    fun uri(): String = "mongodb://$username:$password@$host:$port/$database"
}