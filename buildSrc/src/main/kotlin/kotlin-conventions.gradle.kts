import org.gradle.api.JavaVersion
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.repositories
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
    mavenCentral()
}

plugins {
    kotlin("jvm")
    id("com.diffplug.spotless")
}

spotless {
    kotlin {
        ktlint(libs.versions.ktlint.get())
        target("src/**/*.kt")
    }
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        languageVersion = "1.6"
        apiVersion = "1.6"
        freeCompilerArgs += listOf("-opt-in=kotlin.RequiresOptIn", "-Xcontext-receivers")
    }
}
