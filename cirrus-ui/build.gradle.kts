@file:Suppress("PropertyName")
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val logback_version: String by project
val kotlin_version: String by project

plugins {
    kotlin("jvm") apply true
    application
}

group = "cyou.shinobi9"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":cirrus-core"))

    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlin_version")
    implementation("no.tornado:tornadofx:1.7.20") {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }
    implementation("io.github.rybalkinsd:kohttp-jackson:0.12.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.4") {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.5.1-native-mt")
    implementation("ch.qos.logback:logback-classic:$logback_version")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application.mainClass.set("cyou.shinobi9.cirrus.ui.CirrusUIApplicationKt")

tasks.withType<Jar> {
    manifest {
        attributes(
            mapOf(
                "Class-Path" to configurations.compileClasspath.get().joinToString(" ") { it.name },
                "Main-Class" to application.mainClass.get()
            )
        )
    }
}
