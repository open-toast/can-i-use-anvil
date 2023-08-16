plugins {
    id("com.toasttab.can-i-use-anvil")
}

subprojects {
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
                includeModule("com.toasttab.anvil", "can-i-use-anvil-processor")
            }
        }

        mavenCentral()
    }
}