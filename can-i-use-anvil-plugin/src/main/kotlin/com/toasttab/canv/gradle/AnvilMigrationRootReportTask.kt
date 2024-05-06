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
    abstract val reportFiles: ConfigurableFileCollection

    @OutputFile
    lateinit var outputFile: File

    @TaskAction
    fun execute() {
        val reports = reportFiles.mapNotNull {
            if (it.exists()) {
                AnvilMigrationReport.decodeFromString(it.readText())
            } else {
                logger.warn("report $it does not exist, possibly project has no sources")
                null
            }
        }.sortedBy { it.project }.partition { it.blockers.isEmpty() }

        val report = AggregatedAnvilMigrationReport(
            reports.first.map { it.project },
            reports.second
        )

        outputFile.writeText(report.encodeToString())
    }
}
