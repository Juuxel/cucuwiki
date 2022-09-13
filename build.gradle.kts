import com.github.gradle.node.pnpm.task.PnpmTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

plugins {
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
    id("com.github.node-gradle.node") version "3.4.0"
    idea
    id("org.cadixdev.licenser") version "0.6.1"
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
    implementation(libs.jsoup)

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

val tsOutput = file("build/scripts")
val tsBundled = file("build/script.js")

fun pnpm(name: String, vararg command: String, block: PnpmTask.() -> Unit): TaskProvider<PnpmTask> =
    tasks.register<PnpmTask>(name) {
        dependsOn(pnpmInstall)
        args.addAll(*command)
        block()
    }

val pnpmInstall = tasks.getByName("pnpmInstall") {
    inputs.file("package.json")
    outputs.file("pnpm-lock.yaml")
    outputs.dir("node_modules")
}
val checkTypeScript = pnpm(name = "checkTypeScript", "tsc", "--noEmit") {
    inputs.dir(file("src/main/typescript"))
    inputs.file(file("tsconfig.json"))
}
val rollup = pnpm(name = "rollup", "rollup", "-c") {
    inputs.file("rollup.config.js")
    inputs.dir("src/main/typescript")
    outputs.file(tsBundled)
}

tasks.processResources {
    dependsOn(rollup)
    from(tsBundled) {
        into("static")
    }
}

val eslint = pnpm(name = "eslint", "eslint", "src/main/typescript") {
    inputs.files(".eslintrc.json")
    inputs.dir("src/main/typescript")
}

tasks.check {
    dependsOn(checkTypeScript, eslint)
}

idea {
    module {
        sourceDirs.add(file("src/main/typescript"))
    }
}

license {
    header(file("HEADER.txt"))
    style.put("ts", "BLOCK_COMMENT")

    tasks.register("ts") {
        files.from(fileTree("src/main/typescript"))
    }

    // The headers will end up in the final files and clutter it.
    exclude("**/*.peb.html")
}
