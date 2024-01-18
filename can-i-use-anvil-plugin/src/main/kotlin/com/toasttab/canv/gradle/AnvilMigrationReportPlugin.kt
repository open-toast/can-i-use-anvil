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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.internal.KaptTask
import org.jetbrains.kotlin.gradle.plugin.KaptExtension

class AnvilMigrationReportPlugin : Plugin<Project> {
    override fun apply(root: Project) {
        val extension = root.extensions.create<AnvilMigrationExtension>("anvilMigration")

        val rootReportTask = root.tasks.register<AnvilMigrationRootReportTask>("anvilMigrationReport") {
            output = project.layout.buildDirectory.file("anvil-aggregated-report.json").get().asFile
        }

        if (root.gradle.startParameter.taskNames.contains("anvilMigrationReport")) {
            root.allprojects.forEach { sub ->
                sub.afterEvaluate {
                    configurations.kapt {
                        if (it.hasDagger()) {
                            val output = layout.buildDirectory.file("anvil-report.json").get().asFile

                            configure<KaptExtension> {
                                arguments {
                                    arg("anvil.migration.project", sub.name)
                                    arg("anvil.migration.report", "file://$output")
                                }
                            }

                            tasks.withType<KaptTask> {
                                if (extension.matches(name)) {
                                    outputs.file(output).withPropertyName("anvil-report-output")

                                    addReportTask(rootReportTask, output, this)
                                }
                            }

                            it.addDependency("com.toasttab.canv:can-i-use-anvil-processor:${BuildConfig.VERSION}")
                        }
                    }
                }
            }
        }
    }

    private fun addReportTask(reportTask: TaskProvider<AnvilMigrationRootReportTask>, output: Any, kaptTask: KaptTask) {
        reportTask.configure {
            dependsOn(kaptTask)
            reports.from(output)
        }
    }

    private fun ConfigurationContainer.kapt(f: (Configuration) -> Unit) = matching { it.name == "kapt" }.forEach(f)

    private fun Configuration.hasDagger() = dependencies.any { dep ->
        dep.group == "com.google.dagger" && dep.name == "dagger-compiler"
    }

    context(Project)
    private fun Configuration.addDependency(spec: String) {
        dependencies.add(this@Project.dependencies.create(spec))
    }
}
