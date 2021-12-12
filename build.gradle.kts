
group = "de.hglabor"
version = "0.2.0"

plugins {
    kotlin("jvm") version "1.6.0"
}

repositories {
    mavenLocal()
    mavenCentral()
    // Paper
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://repo.codemc.io/repository/maven-public/")
    // FAWE
    maven("https://mvn.intellectualsites.com/content/repositories/releases/")
    // CloudNet
    maven("https://repo.cloudnetservice.eu/repository/releases/")
}

dependencies {
    implementation(kotlin("reflect"))
    // CraftBukkit
    compileOnly("org.bukkit", "craftbukkit", "1.17-R0.1-SNAPSHOT")
    // PAPER
    compileOnly("io.papermc.paper:paper-api:1.17-R0.1-SNAPSHOT")
    // FAWE
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit:1.17-418")
    // KSPIGOT
    implementation("net.axay:kspigot:1.17.4")
    // HGLabor Utils
    implementation("de.hglabor:hglabor-utils:0.0.17")
    // CloudNet
    compileOnly("de.dytanic.cloudnet", "cloudnet-bridge", "3.4.0-RELEASE")
}

tasks {
    compileJava {
        options.release.set(16)
        options.encoding = "UTF-8"
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "16"
    }
}