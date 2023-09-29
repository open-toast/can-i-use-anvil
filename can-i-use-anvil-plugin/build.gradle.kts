import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    `kotlin-dsl`
    `kotlin-conventions`
    `plugin-publishing-conventions`
    alias(libs.plugins.build.config)
    alias(libs.plugins.testkit.plugin)
}

buildConfig {
    packageName.set("com.toasttab.canv.gradle")
    buildConfigField("String", "VERSION", "\"$version\"")
}

testkitTests {
    replaceToken("DAGGER_VERSION", libs.versions.dagger.get())
    replaceToken("TESTKIT_PLUGIN_VERSION", libs.versions.testkit.plugin.get())
    replaceToken("PROCESSOR_LIBS", projects.canIUseAnvilProcessor.dependencyProject.file("build/libs").toString())
}

tasks {
    test {
        useJUnitPlatform()

        dependsOn(":can-i-use-anvil-processor:shadowJar")
    }
}

dependencies {
    implementation(gradleApi())
    implementation(libs.kotlin.gradle)
    implementation(projects.canIUseAnvilModel)

    testImplementation(gradleTestKit())
    testImplementation(libs.junit)
    testImplementation(libs.strikt.core)
    testImplementation(libs.testkit.junit5)
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
