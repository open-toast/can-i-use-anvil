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

import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

sealed interface AnvilMigrationBlocker {
    class Component(
        val type: TypeElement,
    ) : AnvilMigrationBlocker {
        override fun toString() = "$type is a Component"
    }

    class JavaModule(
        val type: TypeElement,
    ) : AnvilMigrationBlocker {
        override fun toString() = "$type is a non-Kotlin Module"
    }

    class JavaSingleton(
        val type: TypeElement,
    ) : AnvilMigrationBlocker {
        override fun toString() = "$type is a non-Kotlin Singleton"
    }

    class JavaMemberInjection(
        val type: TypeElement,
    ) : AnvilMigrationBlocker {
        override fun toString() = "$type is a non-Kotlin class that needs member injection"
    }

    class InheritedJavaMemberInjection(
        val type: TypeElement,
        val declaredIn: Set<Element>,
    ) : AnvilMigrationBlocker {
        override fun toString() = "$type inherits @Injected members from non-Kotlin $declaredIn"
    }
}
