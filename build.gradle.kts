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

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "1.8"

// val compileTestKotlin: KotlinCompile by tasks
// compileTestKotlin.kotlinOptions.jvmTarget = "1.8"

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}

tasks.compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}

dependencies {
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-websockets:$ktor_version")
    implementation("io.ktor:ktor-client-logging-jvm:$ktor_version")
    implementation("io.ktor:ktor-client-serialization:$ktor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.8")

    testImplementation("no.tornado:tornadofx:1.7.20")

//    implementation("io.ktor:ktor-client-json-jvm:$ktor_version")
//    implementation("io.ktor:ktor-client-gson:$ktor_version")
//    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
//    implementation("io.ktor:ktor-server-netty:$ktor_version")
//    implementation("io.ktor:ktor-server-core:$ktor_version")
//    implementation("io.ktor:ktor-websockets:$ktor_version")
//    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
}
