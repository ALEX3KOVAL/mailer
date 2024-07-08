import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.serialization)
}

group = "group.ost.mailer"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation(libs.simplejavamail)
    implementation(libs.charleskorn.kaml)
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
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}