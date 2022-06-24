plugins {
    `kotlin-dsl`
    `kotlin-conventions`
    `publishing-conventions`
    alias(libs.plugins.gradle.publish)
    alias(libs.plugins.build.config)
}

buildConfig {
    packageName.set("com.toasttab.anvil.gradle")
    buildConfigField("String", "VERSION", "\"$version\"")

    sourceSets.getByName("test") {
        buildConfigField("String", "DAGGER_COMPILER", "\"${libs.dagger.compiler.get()}\"")
        buildConfigField("String", "KOTLIN_VERSION", "\"${libs.versions.kotlin.get()}\"")
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

dependencies {
    implementation(gradleApi())

    testImplementation(gradleTestKit())
    testImplementation(libs.junit)
    testImplementation(libs.truth)
}

gradlePlugin {
    isAutomatedPublishing = false

    plugins {
        create("can-i-use-anvil") {
            id = "com.toasttab.can-i-use-anvil"
            implementationClass = "com.toasttab.anvil.gradle.AnvilMigrationPlugin"
        }
    }
}

pluginBundle {
    mavenCoordinates {
        group = "${project.group}"
    }
    website = ProjectInfo.url
    vcsUrl = ProjectInfo.url
    description = ProjectInfo.description
    tags = listOf("dagger", "anvil")
}

ext[com.gradle.publish.PublishTask.GRADLE_PUBLISH_KEY] = System.getenv("GRADLE_PORTAL_PUBLISH_KEY")
ext[com.gradle.publish.PublishTask.GRADLE_PUBLISH_SECRET] = System.getenv("GRADLE_PORTAL_PUBLISH_SECRET")

tasks.named("publishPlugins") {
    enabled = isRelease()
}
