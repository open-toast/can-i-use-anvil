plugins {
    `kotlin-conventions`
    kotlin("kapt")
    `publishing-conventions`
}

dependencies {
    kapt(libs.autoservice.processor)
    implementation(kotlin("stdlib"))
    implementation(libs.autoservice.annotations)
    implementation(libs.dagger)
}