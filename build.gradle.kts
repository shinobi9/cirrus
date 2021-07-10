import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val logback_version: String by project
val ktor_version: String by project
val kotlin_version: String by project

plugins {
    kotlin("jvm") version "1.5.20"
    kotlin("plugin.serialization") version "1.5.20"
}

group = "cyou.shinobi9"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

val java8 = JavaVersion.VERSION_1_8.toString()

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
        jvmTarget = java8
        sourceCompatibility = java8
        targetCompatibility = java8
    }
}

dependencies {
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-client-core:$ktor_version") {
        exclude("org.jetbrains.kotlinx", "kotlinx-coroutines-core")
    }
    implementation("io.ktor:ktor-client-cio:$ktor_version") {
        exclude("org.jetbrains.kotlinx", "kotlinx-coroutines-core")
    }
    implementation("io.ktor:ktor-client-websockets:$ktor_version")
    implementation("io.ktor:ktor-client-logging-jvm:$ktor_version")
    implementation("io.ktor:ktor-client-serialization:$ktor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0-native-mt")
    testImplementation("no.tornado:tornadofx:1.7.20") {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }
}
