plugins {
    kotlin("jvm") version "1.5.21" apply true
    id("maven-publish")
}

repositories {
    mavenCentral()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
    maven { url = uri("https://kotlin.bintray.com/kotlinx") }
}