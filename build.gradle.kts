import com.github.gradle.node.npm.task.NpxTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

plugins {
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
    id("com.github.node-gradle.node") version "3.4.0"
    idea
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))
    implementation(libs.javalin)
    implementation(libs.nightconfig.toml)
    implementation(libs.jgit)
    implementation(libs.slf4j)
    runtimeOnly(libs.logback)
    implementation(libs.bundles.flexmark)
    implementation(libs.pebble)
    implementation(libs.kotlinx.serialization)

    testImplementation(libs.atrium)
    testImplementation(libs.spek.dsl.jvm)
    testRuntimeOnly(libs.spek.runner.junit5)
}

tasks.test {
    useJUnitPlatform {
        includeEngines("spek2")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

val createJarInJarConfig by tasks.register("createJarInJarConfig") {
    dependsOn(tasks.jar)
    val file = buildDir.resolve("libs").resolve("jar-in-jar.properties")
    outputs.file(file)

    doLast {
        file.parentFile.mkdirs()
        val props = Properties()
        props.setProperty("main-class", "juuxel.cucuwiki.MainKt")
        val mainJar = tasks.jar.get().archiveFile.get().asFile
        val jars = configurations.getByName("runtimeClasspath").resolve() + mainJar
        props.setProperty("jar-names", jars.joinToString(separator = ",") { it.name })
        file.writer(Charsets.UTF_8).use {
            props.store(it, null)
        }
    }
}

evaluationDependsOn(":jar-in-jar")

val jarInJar = tasks.register<Jar>("jarInJar") {
    archiveClassifier.set("bundled")

    // Set up JAR-in-JAR
    val libraryJar = project(":jar-in-jar").tasks.named<Jar>("jar")
    dependsOn(libraryJar, createJarInJarConfig)
    from(zipTree(libraryJar.flatMap { it.archiveFile }))
    from(createJarInJarConfig.outputs.files) {
        into("META-INF")
    }

    // Add JARs
    from(configurations.getByName("runtimeClasspath")) {
        into("META-INF/jars")
    }
    dependsOn(tasks.jar)
    from(tasks.jar.flatMap { it.archiveFile }) {
        into("META-INF/jars")
    }

    // Set up main class
    manifest {
        attributes("Main-Class" to "juuxel.jarinjar.JarInJarLauncher")
    }
}

tasks.assemble {
    dependsOn(jarInJar)
}

val tsOutput = file("build/scripts/script.js")

val npmCi = tasks.getByName("npm_ci") {
    outputs.dir("node_modules")
}
val compileTypeScript = tasks.register<NpxTask>("compileTypeScript") {
    dependsOn(npmCi)
    command.set("tsc")
    inputs.dir(file("src/main/typescript"))
    outputs.file(tsOutput)
}

tasks.processResources {
    dependsOn(compileTypeScript)
    from(tsOutput) {
        into("static")
    }
}

idea {
    module {
        sourceDirs.add(file("src/main/typescript"))
    }
}
