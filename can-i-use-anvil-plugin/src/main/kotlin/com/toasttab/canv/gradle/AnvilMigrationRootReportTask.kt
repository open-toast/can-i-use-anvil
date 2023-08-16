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
import com.toasttab.canv.model.AnvilMigrationReport
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class AnvilMigrationRootReportTask : DefaultTask() {
    @get:InputFiles
    abstract val reports: ConfigurableFileCollection

    @OutputFile
    lateinit var output: File

    @TaskAction
    fun execute() {
        val report = AggregatedAnvilMigrationReport(
            reports.map {
                AnvilMigrationReport.decodeFromString(it.readText())
            },
        )

        output.writeText(report.encodeToString())
    }
}
