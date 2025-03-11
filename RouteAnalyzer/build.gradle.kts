plugins {
    kotlin("jvm") version "2.1.10"
}

group = "polito.it.wa2.g04.lab01"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(23)
}