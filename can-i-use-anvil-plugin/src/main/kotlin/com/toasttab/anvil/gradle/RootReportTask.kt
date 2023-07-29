package com.toasttab.anvil.gradle

import com.toasttab.anvil.model.AggregatedAnvilMigrationReport
import com.toasttab.anvil.model.AnvilMigrationReport
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class RootReportTask : DefaultTask() {
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
