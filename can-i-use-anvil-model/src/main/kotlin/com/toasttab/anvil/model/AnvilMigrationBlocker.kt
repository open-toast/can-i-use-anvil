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

package com.toasttab.anvil.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val JSON = Json { }

@Serializable
data class AggregatedAnvilMigrationReport(
    val reports: List<AnvilMigrationReport>,
) {
    fun encodeToString() = JSON.encodeToString(this)

    companion object {
        fun decodeFromString(value: String): AggregatedAnvilMigrationReport = JSON.decodeFromString(value)
    }
}

@Serializable
data class AnvilMigrationReport(
    val project: String,
    val blockers: List<AnvilMigrationBlocker>,
) {
    fun encodeToString() = JSON.encodeToString(this)

    override fun toString() =
        if (blockers.isEmpty()) {
            "Can use Anvil"
        } else {
            "Cannot use Anvil because $blockers"
        }

    companion object {
        fun decodeFromString(value: String): AnvilMigrationReport = JSON.decodeFromString(value)
    }
}

@Serializable
sealed interface AnvilMigrationBlocker {
    @Serializable
    @SerialName("component")
    data class Component(
        val typeName: String,
    ) : AnvilMigrationBlocker {
        override fun toString() = "$typeName is a Component"
    }

    @Serializable
    @SerialName("module")
    data class JavaModule(
        val typeName: String,
    ) : AnvilMigrationBlocker {
        override fun toString() = "$typeName is a non-Kotlin Module"
    }

    @Serializable
    @SerialName("java-singleton")
    data class JavaSingleton(
        val typeName: String,
    ) : AnvilMigrationBlocker {
        override fun toString() = "$typeName is a non-Kotlin Singleton"
    }

    @Serializable
    @SerialName("java-member-injection")
    data class JavaMemberInjection(
        val typeName: String,
    ) : AnvilMigrationBlocker {
        override fun toString() = "$typeName is a non-Kotlin class that needs member injection"
    }

    @Serializable
    @SerialName("inherited-java-member-injection")
    data class InheritedJavaMemberInjection(
        val typeName: String,
        val declaredIn: Set<String>,
    ) : AnvilMigrationBlocker {
        override fun toString() = "$typeName inherits @Injected members from non-Kotlin $declaredIn"
    }
}
