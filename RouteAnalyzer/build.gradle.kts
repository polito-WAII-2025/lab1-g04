plugins {
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.serialization") version "2.1.10"
    id("com.gradleup.shadow") version "8.3.6"
    application
}

group = "it.polito.wa2.g04"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("com.charleskorn.kaml:kaml:0.49.0")
    implementation("com.uber:h3:4.0.1")
    implementation("org.locationtech.jts:jts-core:1.20.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes["Main-Class"]  = "it.polito.wa2.g04.MainKt"
    }
}

kotlin {
    jvmToolchain(23)
}

application {
    mainClass = "it.polito.wa2.g04.MainKt"
}