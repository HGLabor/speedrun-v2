group = "de.hglabor"
version = "0.2.2"
val kspigot = "1.18.0"
val kutils = "0.0.19"
val hglaborUtils = "0.0.17"
val kotlinxSerializationJson = "1.3.2"
val kmongo = "4.4.0"

plugins {
    kotlin("jvm") version "1.6.0"
    kotlin("plugin.serialization") version "1.6.10"
    id("io.papermc.paperweight.userdev") version "1.3.2"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
}

bukkit {
    main = "de.hglabor.speedrun.Speedrun"
    website = "https://github.com/HGLabor/speedrun-v2"
    version = project.version.toString()
    apiVersion = "1.18"
    loadBefore = listOf("WorldEdit")
    libraries = listOf(
        "net.axay:kspigot:$kspigot",
        "de.hglabor.utils:kutils:$kutils",
        "org.jetbrains.kotlin:kotlin-reflect:1.6.0",
        "de.hglabor:hglabor-utils:$hglaborUtils",
        "org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationJson",

        "org.litote.kmongo:kmongo:$kmongo",
        "org.litote.kmongo:kmongo-serialization-mapping:$kmongo",
        "org.litote.kmongo:kmongo-serialization:$kmongo"
    )
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.cloudnetservice.eu/repository/snapshots/") // CloudNet
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    paperDevBundle("1.18.1-R0.1-SNAPSHOT")
    implementation("net.axay:kspigot:$kspigot")
    implementation("de.hglabor.utils:kutils:$kutils")
    implementation("de.hglabor:hglabor-utils:$hglaborUtils")
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationJson")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.2.8")
    compileOnly("de.dytanic.cloudnet", "cloudnet-bridge", "3.4.0-SNAPSHOT")
    compileOnly("de.dytanic.cloudnet", "cloudnet-wrapper-jvm", "3.4.0-SNAPSHOT")

    implementation("org.litote.kmongo:kmongo:$kmongo")
    implementation("org.litote.kmongo:kmongo-serialization-mapping:$kmongo")
    implementation("org.litote.kmongo:kmongo-serialization:$kmongo")
}

tasks {
    build {
        dependsOn(reobfJar)
    }
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(17)
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }
}