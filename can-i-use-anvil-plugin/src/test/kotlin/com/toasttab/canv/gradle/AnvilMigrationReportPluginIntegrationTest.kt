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

package com.toasttab.canv.gradle

import com.toasttab.canv.model.AggregatedAnvilMigrationReport
import com.toasttab.canv.model.AnvilMigrationBlocker
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import strikt.api.expectThat
import strikt.assertions.contains
import strikt.assertions.containsExactly
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo
import java.io.File

@ExtendWith(TestProjectExtension::class)
class AnvilMigrationReportPluginIntegrationTest {
    @Test
    fun `processor dependency added to the kapt configuration when using dagger`(project: TestProject) {
        val result = GradleRunner.create()
            .withProjectDir(project.dir)
            .withArguments("dependencies", "--configuration=kapt")
            .withPluginClasspath()
            .build()

        expectThat(result.output).contains(
            "com.toasttab.canv:can-i-use-anvil-processor:${BuildConfig.VERSION}",
        )
    }

    @Test
    fun `processor dependency not added to the kapt configuration when not using dagger`(project: TestProject) {
        val result = GradleRunner.create()
            .withProjectDir(project.dir)
            .withArguments("dependencies", "--configuration=kapt")
            .withPluginClasspath()
            .build()

        expectThat(result.output).not().contains(
            "com.toasttab.canv:can-i-use-anvil-processor",
        )
    }

    @Test
    fun `processor dependency not added without kapt`(project: TestProject) {
        val result = GradleRunner.create()
            .withProjectDir(project.dir)
            .withArguments("dependencies")
            .withPluginClasspath()
            .build()

        expectThat(result.output).not().contains(
            "com.toasttab.canv:can-i-use-anvil-processor",
        )
    }

    @Test
    fun `generates correct aggregated report`(project: TestProject) {
        GradleRunner.create()
            .withProjectDir(project.dir)
            .withArguments("anvilMigrationReport")
            .withPluginClasspath()
            .build()

        val reports = AggregatedAnvilMigrationReport.decodeFromString(File(project.dir, "build/anvil-aggregated-report.json").readText()).reports.sortedBy { it.project }

        expectThat(reports).hasSize(2)
        expectThat(reports[0]) {
            get { blockers }.containsExactly(
                AnvilMigrationBlocker.JavaModule("com.toasttab.canv.test.AppModule"),
            )
            get { this.project }.isEqualTo("project1")
        }
        expectThat(reports[1]) {
            get { blockers }.containsExactly(
                AnvilMigrationBlocker.Component("com.toasttab.canv.test.AppComponent"),
            )

            get { this.project }.isEqualTo("project2")
        }
    }
}
