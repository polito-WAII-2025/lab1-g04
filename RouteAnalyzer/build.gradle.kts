plugins {
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.serialization") version "2.1.10"
    id("com.gradleup.shadow") version "8.3.6"
    application
}

group = "polito.it.wa2.g04.lab01"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("com.charleskorn.kaml:kaml:0.49.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes["Main-Class"]  = "com.polito.it.wa2.g04.lab01.MainKt"
    }
}

kotlin {
    jvmToolchain(23)
}

application {
    mainClass = "com.polito.it.wa2.g04.lab01.MainKt"
}