import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.21")
    implementation("no.tornado:tornadofx:1.7.20") {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }

    implementation("io.github.rybalkinsd:kohttp-jackson:0.12.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.9") {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.5.1")
    implementation("io.github.microutils:kotlin-logging:1.12.0")
    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("ch.qos.logback:logback-classic:1.2.3")
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
//    from(configurations.compile.map { entry -> zipTree(entry) }) {
//        exclude("META-INF/MANIFEST.MF")
//        exclude("META-INF/*.SF")
//        exclude("META-INF/*.DSA")
//        exclude("META-INF/*.RSA")
//    }
}
