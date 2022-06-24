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
import dagger.Component
import dagger.Module
import dagger.Subcomponent
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

const val LOG_FILE = "anvil.migration.logFile"

@AutoService(Processor::class)
class AnvilMigrationAdvisor : AbstractProcessor() {
    private inline fun <reified T : Annotation> Element.hasAnnotation() = getAnnotation(T::class.java) != null
    private fun Element.isKotlin() = hasAnnotation<Metadata>()

    private var firstRound = true

    override fun getSupportedOptions() = setOf(LOG_FILE)

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        if (firstRound) {
            firstRound = false

            val blockers = roundEnv.rootElements.filterIsInstance<TypeElement>().mapNotNull(::blockerType)

            if (blockers.isEmpty()) {
                processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, "Can use Anvil!")
            } else {
                processingEnv.messager.printMessage(Diagnostic.Kind.WARNING, "Cannot use Anvil because $blockers")

                processingEnv.options[LOG_FILE]?.let { file ->
                    processingEnv.filer.createResource(
                        StandardLocation.CLASS_OUTPUT,
                        "",
                        file,
                        *roundEnv.rootElements.toTypedArray(),
                    ).openWriter().use {
                        for (blocker in blockers) {
                            it.write("$blocker\n")
                        }
                    }
                }
            }
        }

        return false
    }

    private fun blockerType(type: TypeElement): AnvilMigrationBlocker? {
        val isKotlin = type.isKotlin()

        return if (!isKotlin && type.hasAnnotation<Singleton>()) {
            AnvilMigrationBlocker.JavaSingleton(type)
        } else if (!isKotlin && type.hasAnnotation<Module>()) {
            AnvilMigrationBlocker.JavaModule(type)
        } else if (type.hasAnnotation<Component>() || type.hasAnnotation<Subcomponent>()) {
            AnvilMigrationBlocker.Component(type)
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
                    AnvilMigrationBlocker.JavaMemberInjection(type)
                } else {
                    AnvilMigrationBlocker.InheritedJavaMemberInjection(type, superTypesWithInject)
                }
            } else {
                null
            }
        }
    }

    override fun getSupportedAnnotationTypes() = setOf("*")

    override fun getSupportedSourceVersion() = SourceVersion.latestSupported()
}
