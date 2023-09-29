plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("com.toasttab.can-i-use-anvil")
    id("com.toasttab.testkit.coverage") version "@TESTKIT_PLUGIN_VERSION@"
}

dependencies {
    kapt("com.google.dagger:dagger-compiler:@DAGGER_VERSION@")
}
