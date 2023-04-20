package com.toasttab.anvil.gradle

abstract class AnvilMigrationExtension {
    var tasks: List<String> = listOf("kaptKotlin")

    fun matches(task: String) = tasks.contains(task)
}
