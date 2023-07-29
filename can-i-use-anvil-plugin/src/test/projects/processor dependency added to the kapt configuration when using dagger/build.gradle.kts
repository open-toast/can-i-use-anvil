plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("com.toasttab.can-i-use-anvil")
}

dependencies {
    kapt("com.google.dagger:dagger-compiler:@DAGGER_VERSION@")
}
