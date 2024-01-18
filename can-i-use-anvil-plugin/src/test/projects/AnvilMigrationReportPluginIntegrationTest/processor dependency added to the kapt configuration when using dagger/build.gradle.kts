plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("com.toasttab.can-i-use-anvil")
    id("com.toasttab.testkit.coverage") version "@TESTKIT_PLUGIN_VERSION@"
}

repositories {
    // use the ivy repository trick to point kapt to the processor uberjar
    // the uberjar is only necessary for tests because we are not doing any proper dependency resolution here
    ivy {
        url = uri("@PROCESSOR_LIBS@")

        patternLayout {
            artifact("[module]-[revision]-all.[ext]")
        }

        metadataSources {
            artifact()
        }

        content {
            includeModule("com.toasttab.canv", "can-i-use-anvil-processor")
        }
    }

    mavenCentral()
}

dependencies {
    kapt("com.google.dagger:dagger-compiler:@DAGGER_VERSION@")
}
