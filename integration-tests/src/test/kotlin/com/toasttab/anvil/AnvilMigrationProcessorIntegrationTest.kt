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

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
class AnvilMigrationProcessorIntegrationTest {
    @Test
    fun `verify blockers`() {
        val lines = Thread.currentThread().contextClassLoader
            .getResourceAsStream("test-log")
            .bufferedReader().readLines()
            .filter { it.isNotEmpty() }
            .sorted()

        assertThat(lines).hasSize(4)

        assertThat(lines[0]).contains("Activity inherits @Injected members")
        assertThat(lines[1]).contains("AppComponent is a Component")
        assertThat(lines[2]).contains("AppModule is a non-Kotlin Module")
        assertThat(lines[3]).contains("BaseActivity is a non-Kotlin class")
    }
}
