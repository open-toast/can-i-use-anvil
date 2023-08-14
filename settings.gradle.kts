enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath("gradle.plugin.net.vivin:gradle-semantic-build-versioning:4.0.0")
    }
}
    
rootProject.name = "can-i-use-anvil"

apply(plugin = "net.vivin.gradle-semantic-build-versioning")

include(
    ":can-i-use-anvil-model",
    ":can-i-use-anvil-processor",
    ":can-i-use-anvil-plugin",
    ":integration-tests",
)
