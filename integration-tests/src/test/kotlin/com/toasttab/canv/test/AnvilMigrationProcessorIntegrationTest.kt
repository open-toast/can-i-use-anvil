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

package com.toasttab.canv.test

import com.toasttab.canv.model.AnvilMigrationBlocker
import com.toasttab.canv.model.AnvilMigrationReport
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.containsExactlyInAnyOrder

class AnvilMigrationProcessorIntegrationTest {
    @Test
    fun `verify blockers`() {
        val report = AnvilMigrationReport.decodeFromString(
            Thread.currentThread().contextClassLoader
                .getResourceAsStream("test-report.json").reader().readText()
        )

        expectThat(report.blockers).containsExactlyInAnyOrder(
            AnvilMigrationBlocker.InheritedJavaMemberInjection(Activity::class.java.name, setOf(BaseActivity::class.java.name)),
            AnvilMigrationBlocker.Component(AppComponent::class.java.name),
            AnvilMigrationBlocker.AndroidInjector(AndroidBindings::class.java.name),
            AnvilMigrationBlocker.JavaMemberInjection(BaseActivity::class.java.name),
            AnvilMigrationBlocker.JavaModule(AppModule::class.java.name)
        )
    }
}
