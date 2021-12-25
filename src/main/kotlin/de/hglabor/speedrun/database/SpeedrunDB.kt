package de.hglabor.speedrun.database

import com.mongodb.*
import com.mongodb.client.*
import com.mongodb.client.model.Filters
import de.hglabor.speedrun.database.data.*
import de.hglabor.utils.kutils.logger
import org.bson.UuidRepresentation
import org.litote.kmongo.KMongo
import org.litote.kmongo.findOne
import org.litote.kmongo.service.ClassMappingType

object SpeedrunDB {
    private lateinit var client: MongoClient
    private lateinit var database: MongoDatabase
    lateinit var recordsCollection: MongoCollection<SpeedrunRecord>
    lateinit var locationsCollection: MongoCollection<Locations>

    fun enable() {
        try {
            DatabaseConfig.loadConfig()
            val connectionString = ConnectionString(DatabaseConfig.uri())
            val clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .codecRegistry(ClassMappingType.codecRegistry(MongoClientSettings.getDefaultCodecRegistry()))
                .build()

            client = KMongo.createClient(
                MongoClientSettings.builder()
                    .uuidRepresentation(UuidRepresentation.STANDARD)
                    .applyConnectionString(connectionString)
                    .build()
            )

            client = MongoClients.create(clientSettings)
            database = client.getDatabase(DatabaseConfig.database)
            recordsCollection = getOrCreateCollection("speedrunv2_records", SpeedrunRecord::class.java)
            locationsCollection = getOrCreateCollection("speedrunv2_locations", Locations::class.java)
            logger.info("Successfully connected to Mongo database '${database.name}'.")
            locations = locationsCollection.findOne() ?: return
        } catch (e: MongoException) {
            e.printStackTrace()
        }
    }

    /**
     * Called upon Server Shutdown.
     */
    fun disable() {
        locationsCollection.deleteMany(Filters.empty())
        locationsCollection.insertOne(locations)
    }

    private inline fun <reified T> getOrCreateCollection(name: String, dataType: Class<T>): MongoCollection<T> {
        if (!database.listCollectionNames().contains(name)) database.createCollection(name)
        return database.getCollection(name, dataType)
    }

    private inline fun <reified T> getOrCreateCollection(name: String): MongoCollection<*> {
        if (!database.listCollectionNames().contains(name)) database.createCollection(name)
        return database.getCollection(name)
    }
}