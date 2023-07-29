import org.apache.tools.ant.filters.ReplaceTokens

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
}

tasks.register<Copy>("copy-projects") {
    from("src/test/projects")
    into("${project.buildDir}/test-projects")

    filter(mapOf("tokens" to mapOf(
        "DAGGER_VERSION" to libs.versions.dagger.get(),
        "PROCESSOR_LIBS" to projects.canIUseAnvilProcessor.dependencyProject.file("build/libs").toString()
    )), ReplaceTokens::class.java)
}

tasks.named<Test>("test") {
    dependsOn(":can-i-use-anvil-processor:shadowJar")
    dependsOn("copy-projects")

    systemProperty("test-projects", "${project.buildDir}/test-projects")

    useJUnitPlatform()
}

dependencies {
    implementation(gradleApi())
    implementation(libs.kotlin.gradle)
    implementation(projects.canIUseAnvilModel)

    testImplementation(gradleTestKit())
    testImplementation(libs.junit)
    testImplementation(libs.strikt.core)
}

gradlePlugin {
    website = ProjectInfo.url
    vcsUrl = ProjectInfo.url

    plugins {
        create("can-i-use-anvil") {
            id = "com.toasttab.can-i-use-anvil"
            implementationClass = "com.toasttab.anvil.gradle.AnvilMigrationPlugin"
            tags = listOf("dagger", "anvil")
            description = ProjectInfo.description
        }
    }
}

ext[com.gradle.publish.PublishTask.GRADLE_PUBLISH_KEY] = System.getenv("GRADLE_PORTAL_PUBLISH_KEY")
ext[com.gradle.publish.PublishTask.GRADLE_PUBLISH_SECRET] = System.getenv("GRADLE_PORTAL_PUBLISH_SECRET")

tasks.named("publishPlugins") {
    enabled = isRelease()
}
