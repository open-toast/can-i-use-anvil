plugins {
    `kotlin-conventions`
    `library-publishing-conventions`
    kotlin("kapt")
    alias(libs.plugins.shadow)
}

dependencies {
    kapt(libs.autoservice.processor)
    implementation(kotlin("stdlib"))
    implementation(libs.autoservice.annotations)
    implementation(libs.dagger.runtime)
    implementation(projects.canIUseAnvilModel)
}