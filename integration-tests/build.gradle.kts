plugins {
    `kotlin-conventions`
    kotlin("kapt")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

kapt {
    arguments {
        arg("anvil.migration.report", "classpath://test-report.json")
    }
}

dependencies {
    kapt(libs.dagger.compiler)
    kapt(projects.canIUseAnvilProcessor)

    implementation(projects.canIUseAnvilModel)
    implementation(libs.dagger.runtime)

    testImplementation(libs.junit)
    testImplementation(libs.strikt.core)
}