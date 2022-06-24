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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
class AnvilMigrationPlugin : Plugin<Project> {
    override fun apply(root: Project) {
        root.allprojects.forEach { p ->
            p.afterEvaluate {
                configurations.kapt().forEach { kapt ->
                    if (kapt.hasDagger()) {
                        addDependency(kapt, "com.toasttab.anvil:can-i-use-anvil-processor:${BuildConfig.VERSION}")
                    }
                }
            }
        }
    }

    private fun ConfigurationContainer.kapt() = matching { conf ->
        conf.name == "kapt"
    }

    private fun Configuration.hasDagger() = dependencies.any { dep ->
        dep.group == "com.google.dagger" && dep.name == "dagger-compiler"
    }

    private fun Project.addDependency(conf: Configuration, spec: String) {
        conf.dependencies.add(dependencies.create(spec))
    }
}
