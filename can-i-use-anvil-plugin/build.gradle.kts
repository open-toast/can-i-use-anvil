import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    `kotlin-dsl`
    `kotlin-conventions`
    `plugin-publishing-conventions`
    alias(libs.plugins.build.config)
}

buildConfig {
    packageName.set("com.toasttab.canv.gradle")
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

tasks {
    test {
        useJUnitPlatform()

        dependsOn(":can-i-use-anvil-processor:shadowJar")
        dependsOn("copy-projects")

        systemProperty("test-projects", "${project.buildDir}/test-projects")
    }
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
    plugins {
        create("can-i-use-anvil") {
            id = "com.toasttab.can-i-use-anvil"
            implementationClass = "com.toasttab.canv.gradle.AnvilMigrationReportPlugin"
            tags = listOf("dagger", "anvil")
            description = ProjectInfo.description
            displayName = ProjectInfo.name
        }
    }
}
