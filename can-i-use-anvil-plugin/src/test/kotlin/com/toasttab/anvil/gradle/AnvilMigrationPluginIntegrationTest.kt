/*
 * Copyright (c) 2023 Toast Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.toasttab.anvil.gradle

import com.google.common.truth.Truth.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class AnvilMigrationPluginIntegrationTest {
    @TempDir
    lateinit var buildDir: File

    private fun buildFile() = File(buildDir, "build.gradle.kts")

    private val commonBuildScript = """
        plugins { 
          kotlin("jvm") version "${TestBuildConfig.KOTLIN_VERSION}"
          kotlin("kapt") version "${TestBuildConfig.KOTLIN_VERSION}"
          id("com.toasttab.can-i-use-anvil")
        }
    """

    @Test
    fun `processor dependency added to the kapt configuration when using dagger`() {
        buildFile().writeText(
            """
                $commonBuildScript
                
                dependencies {
                  kapt("${TestBuildConfig.DAGGER_COMPILER}")
                }
            """,
        )

        val result = GradleRunner.create()
            .withProjectDir(buildDir)
            .withArguments("dependencies", "--configuration=kapt")
            .withPluginClasspath()
            .build()

        assertThat(result.output).contains(
            "com.toasttab.anvil:can-i-use-anvil-processor:${BuildConfig.VERSION}",
        )
    }

    @Test
    fun `processor dependency not added to the kapt configuration when not using dagger`() {
        buildFile().writeText(commonBuildScript)

        val result = GradleRunner.create()
            .withProjectDir(buildDir)
            .withArguments("dependencies", "--configuration=kapt")
            .withPluginClasspath()
            .build()

        assertThat(result.output).doesNotContain(
            "com.toasttab.anvil:can-i-use-anvil-processor",
        )
    }
}
