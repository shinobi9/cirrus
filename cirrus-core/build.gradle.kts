@file:Suppress("PropertyName")
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val logback_version: String by project
val ktor_version: String by project
val kotlin_version: String by project

plugins {
    kotlin("jvm") apply true
    kotlin("plugin.serialization") version "1.5.21"
    `maven-publish`
}

group = "cyou.shinobi9"
version = "0.0.2"

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
    api("io.ktor:ktor-client-core:$ktor_version")
    api("io.ktor:ktor-client-cio:$ktor_version")
    api("io.ktor:ktor-client-websockets:$ktor_version")
    api("io.ktor:ktor-client-logging-jvm:$ktor_version")
    api("io.ktor:ktor-client-serialization:$ktor_version")
//    api("org.jetbrains.kotlinx:atomicfu:0.16.2")
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")
    api("io.github.microutils:kotlin-logging-jvm:2.0.11")
    testImplementation("ch.qos.logback:logback-classic:$logback_version")
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(kotlin.sourceSets["main"].kotlin.srcDirs)
}

publishing {
    publications {
        val release by publications.registering(MavenPublication::class) {
            from(components["kotlin"])
            artifact(sourcesJar.get())
            artifactId = "cirrus-core"
            groupId = "cyou.shinobi9.cirrus-core"
            version = "0.0.2"
        }
    }
}
