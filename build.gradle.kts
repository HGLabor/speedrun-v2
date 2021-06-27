@file:Suppress("PropertyName")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
/*
 * BUILD CONSTANTS
 */
val JVM_VERSION = JavaVersion.VERSION_11
val JVM_VERSION_STRING = JVM_VERSION.versionString
/*
 * PROJECT
 */
group = "de.hglabor"
version = "0.0.1"

/*
 * PLUGINS
 */
plugins {
    kotlin("jvm") version "1.4.21"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    kotlin("plugin.serialization") version "1.4.21"
}

/*
 * DEPENDENCY MANAGEMENT
 */
repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
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
    compileOnly("org.bukkit", "craftbukkit", "1.16.5-R0.1-SNAPSHOT")
    // PAPER
    compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
    // FAWE
    compileOnly("com.intellectualsites.fawe:FAWE-Bukkit:1.16-637")
    // KSPIGOT
    implementation("net.axay:kspigot:1.16.26")
    // HGLabor Utils
    implementation("de.hglabor:hglabor-utils:0.0.6")
    // WorldGeneratorApi
    compileOnly("nl.rutgerkok:worldgeneratorapi:1.1.4")
    // CloudNet
    compileOnly("de.dytanic.cloudnet", "cloudnet-bridge", "3.3.0-RELEASE")
}

/*
 * BUILD
 */

// JVM VERSION

java.sourceCompatibility = JVM_VERSION
java.targetCompatibility = JVM_VERSION

tasks.withType<KotlinCompile> {
    configureJvmVersion()
}

// SHADOW

tasks {
    shadowJar {
        simpleRelocate("net.axay.kspigot")
    }
}

/*
 * EXTENSIONS
 */

val JavaVersion.versionString
    get() = majorVersion.let {
        val version = it.toInt()
        if (version <= 10) "1.$it" else it
    }

fun KotlinCompile.configureJvmVersion() {
    kotlinOptions.jvmTarget = JVM_VERSION_STRING
}

fun ShadowJar.simpleRelocate(pattern: String) {
    relocate(pattern, "${project.group}.${project.name.toLowerCase()}.shadow.$pattern")
}
