plugins {
    `kotlin-conventions`
    kotlin("kapt")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

kapt {
    arguments {
        arg("anvil.migration.logFile", "test-log")
    }
}

dependencies {
    kapt(libs.dagger.compiler)
    kapt(projects.canIUseAnvilProcessor)

    implementation(libs.dagger)

    testImplementation(libs.junit)
    testImplementation(libs.truth)
}