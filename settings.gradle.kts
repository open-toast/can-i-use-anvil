enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "can-i-use-anvil"

include(
    ":can-i-use-anvil-model",
    ":can-i-use-anvil-processor",
    ":can-i-use-anvil-plugin",
    ":integration-tests",
)
