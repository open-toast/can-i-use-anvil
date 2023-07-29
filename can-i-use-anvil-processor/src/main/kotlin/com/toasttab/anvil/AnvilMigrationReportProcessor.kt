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

package com.toasttab.anvil

import com.google.auto.service.AutoService
import com.toasttab.anvil.model.AnvilMigrationBlocker
import com.toasttab.anvil.model.AnvilMigrationReport
import dagger.Component
import dagger.Module
import dagger.Subcomponent
import java.io.FileWriter
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.inject.Inject
import javax.inject.Singleton
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
import javax.tools.StandardLocation

const val REPORT_FILE = "anvil.migration.report"
const val PROJECT_NAME = "anvil.migration.project"

@AutoService(Processor::class)
class AnvilMigrationReportProcessor : AbstractProcessor() {
    private inline fun <reified T : Annotation> Element.hasAnnotation() = getAnnotation(T::class.java) != null
    private fun Element.isKotlin() = hasAnnotation<Metadata>()

    private var firstRound = true

    override fun getSupportedOptions() = setOf(REPORT_FILE, PROJECT_NAME)

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        if (firstRound) {
            firstRound = false

            val report = AnvilMigrationReport(
                processingEnv.options[PROJECT_NAME] ?: "",
                roundEnv.rootElements.filterIsInstance<TypeElement>().mapNotNull(::blockerType),
            )

            processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, "$report")

            processingEnv.options[REPORT_FILE]?.let { file ->
                val writer = when (val location = OutputLocation.from(file)) {
                    is OutputLocation.ClasspathLocation -> processingEnv.filer.createResource(
                        StandardLocation.CLASS_OUTPUT,
                        "",
                        location.path,
                        *roundEnv.rootElements.toTypedArray(),
                    ).openWriter()

                    is OutputLocation.AbsoluteLocation -> FileWriter(location.path)
                }

                writer.use {
                    it.write(report.encodeToString())
                }
            }
        }

        return false
    }

    private fun blockerType(type: TypeElement): AnvilMigrationBlocker? {
        val isKotlin = type.isKotlin()

        return if (!isKotlin && type.hasAnnotation<Singleton>()) {
            AnvilMigrationBlocker.JavaSingleton("$type")
        } else if (!isKotlin && type.hasAnnotation<Module>()) {
            AnvilMigrationBlocker.JavaModule("$type")
        } else if (type.hasAnnotation<Component>() || type.hasAnnotation<Subcomponent>()) {
            AnvilMigrationBlocker.Component("$type")
        } else {
            val superTypesWithInject = processingEnv.elementUtils.getAllMembers(type).mapNotNull { member ->
                val declaredIn = member.enclosingElement

                val isKotlinDeclaration = if (declaredIn == type) isKotlin else declaredIn.isKotlin()

                if (member.hasAnnotation<Inject>() && !isKotlinDeclaration) {
                    declaredIn
                } else {
                    null
                }
            }.toSet()

            if (superTypesWithInject.isNotEmpty()) {
                if (superTypesWithInject.contains(type)) {
                    AnvilMigrationBlocker.JavaMemberInjection("$type")
                } else {
                    AnvilMigrationBlocker.InheritedJavaMemberInjection("$type", superTypesWithInject.map { "$it" }.toSet())
                }
            } else {
                null
            }
        }
    }

    override fun getSupportedAnnotationTypes() = setOf("*")

    override fun getSupportedSourceVersion() = SourceVersion.latestSupported()
}
