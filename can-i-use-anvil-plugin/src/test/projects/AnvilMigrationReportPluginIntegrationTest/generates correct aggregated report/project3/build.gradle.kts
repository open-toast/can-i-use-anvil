plugins {
    kotlin("jvm")
    kotlin("kapt")
}

dependencies {
    implementation("com.google.dagger:dagger:@DAGGER_VERSION@")
    kapt("com.google.dagger:dagger-compiler:@DAGGER_VERSION@")
}