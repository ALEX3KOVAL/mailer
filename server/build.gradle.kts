import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.serialization)
    id("io.ktor.plugin") version "3.0.0-beta-1"
}

val exposed_version: String by project
val ktor_version: String by project
val logback_version: String by project

group = "group.ost.mailer"
version = "0.0.1"

repositories {
    mavenCentral()
}

application {
    mainClass.set("group.ost.mailer.server.MainKt")
    val isDevelopment = project.ext.has("development")
    applicationDefaultJvmArgs = listOf(
        "-Dio.ktor.development=$isDevelopment"
    )
}

dependencies {
    testImplementation(kotlin("test"))

    implementation(project(":domain"))

    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-json:$exposed_version")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.postgresql:postgresql:42.7.3")

    implementation("io.ktor:ktor-server-cio-jvm")

    implementation("io.insert-koin:koin-logger-slf4j:3.6.0-Beta4")

    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-server-auth-jwt-jvm")
    implementation("io.ktor:ktor-server-cors-jvm")
    implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

    implementation("io.ktor:ktor-client-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jvm:$ktor_version")
    implementation("io.insert-koin:koin-ktor:3.6.0-Beta4")
    implementation("io.ktor:ktor-client-logging-jvm:$ktor_version")
    implementation(libs.kotlinx.serialization.jvm)
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_21.toString()
            javaParameters = true
            freeCompilerArgs = listOf(
                "-opt-in=" + listOf(
                    "kotlin.RequiresOptIn",
                    "kotlinx.serialization.ExperimentalSerializationApi"
                ).joinToString(",")
            )
        }
    }
    val copyDeps = register("copyDeps", Copy::class) {
        from(configurations.runtimeClasspath.get())
        into(rootProject.layout.buildDirectory.dir("libs"))
        outputs.upToDateWhen { true }
    }
    println("static: ${rootProject.projectDir}/static")
    val copyStatic = register("copyStatic", Copy::class) {
        from("${rootProject.projectDir}/static") {
            exclude("**/*.scss", "**/*.sass")
        }
        into("${rootProject.layout.buildDirectory.get()}/static")
        outputs.upToDateWhen { true }
    }
    jar {
        destinationDirectory.set(rootProject.layout.buildDirectory.get())
        archiveFileName.set("mailer.jar")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        from(sourceSets.main.get().output)

        dependsOn(configurations.runtimeClasspath)
        from({
            configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
        })
        manifest {
            attributes.apply {
                put("Main-Class", application.mainClass)
                put("Class-Path", configurations.runtimeClasspath.get()
                    .filter { it.extension == "jar" }
                    .distinctBy { it.name }
                    .joinToString(separator = " ", transform = { "libs/${it.name}" }))
            }
        }
        dependsOn(copyDeps)
        dependsOn(copyStatic)
    }
    "build" {
        dependsOn(copyStatic)
    }
}

tasks.test {
    useJUnitPlatform()
}