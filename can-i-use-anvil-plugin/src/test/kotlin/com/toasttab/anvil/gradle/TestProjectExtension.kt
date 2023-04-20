package com.toasttab.anvil.gradle

import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import java.io.Closeable
import java.io.File
import kotlin.io.path.createTempDirectory

private val NAMESPACE = ExtensionContext.Namespace.create("gradle-test-project")

class TestProject(
    val dir: File,
) : Closeable {
    override fun close() {
        dir.deleteRecursively()
    }

    val buildFile: File get() = File(dir, "build.gradle.kts")
}

class TestProjectExtension : ParameterResolver, BeforeEachCallback {
    override fun beforeEach(context: ExtensionContext) {
        project(context)
    }

    private fun project(context: ExtensionContext) =
        context.getStore(NAMESPACE).getOrComputeIfAbsent("project") {
            val name = context.requiredTestMethod.name
            val tempProjectDir = createTempDirectory("junit-gradlekit").toFile()

            File("${System.getProperty("test-projects")}/$name").copyRecursively(target = tempProjectDir)

            TestProject(tempProjectDir)
        }

    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext) =
        parameterContext.parameter.type == TestProject::class.java

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext) = project(extensionContext)
}
