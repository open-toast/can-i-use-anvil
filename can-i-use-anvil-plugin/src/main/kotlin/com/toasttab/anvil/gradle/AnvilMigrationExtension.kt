package com.toasttab.anvil.gradle

abstract class AnvilMigrationExtension {
    var kaptTasks: List<String> = listOf("kaptKotlin")

    fun matches(task: String) = kaptTasks.contains(task)
}
