plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.kotlin.gradle)
    implementation(libs.nexus.staging)
    implementation(libs.spotless)
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}