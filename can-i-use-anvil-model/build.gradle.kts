plugins {
    `kotlin-conventions`
    `publishing-conventions`
    alias(libs.plugins.serialization)
}

dependencies {
    implementation(libs.serialization.json)
    testImplementation(libs.junit)
    testImplementation(libs.strikt.core)
}