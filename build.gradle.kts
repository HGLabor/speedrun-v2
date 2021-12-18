group = "de.hglabor"
version = "0.2.1"
val kspigot = "1.18.0"
val kutils = "0.0.2"
val hglaborUtils = "0.0.17"

plugins {
    kotlin("jvm") version "1.6.0"
    id("io.papermc.paperweight.userdev") version "1.3.2"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
}

bukkit {
    main = "de.hglabor.speedrun.Speedrun"
    website = "https://github.com/HGLabor/speedrun-v2"
    version = project.version.toString()
    apiVersion = "1.18"
    libraries = listOf(
        "net.axay:kspigot:$kspigot",
        "de.hglabor.utils:kutils:$kutils",
        "org.jetbrains.kotlin:kotlin-reflect:1.6.0",
        "de.hglabor:hglabor-utils:$hglaborUtils"
    )
}

repositories {
    mavenCentral()
    maven("https://repo.cloudnetservice.eu/repository/releases/") // CloudNet
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    paperDevBundle("1.18.1-R0.1-SNAPSHOT")
    implementation("net.axay:kspigot:1.18.0")
    implementation("de.hglabor.utils:kutils:$kutils")
    implementation("de.hglabor:hglabor-utils:$hglaborUtils")
    implementation(kotlin("reflect"))
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.2.8")
    compileOnly("de.dytanic.cloudnet", "cloudnet-bridge", "3.4.0-RELEASE")
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