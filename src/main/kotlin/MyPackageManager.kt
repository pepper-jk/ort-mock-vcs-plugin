/*
 * Copyright (C) 2023 The ORT Project Authors (see <https://github.com/oss-review-toolkit/ort/blob/main/NOTICE>)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * License-Filename: LICENSE
 */

package org.ossreviewtoolkit.plugins.packagemanagers.mypackagemanager

import java.io.File

import org.ossreviewtoolkit.analyzer.AbstractPackageManagerFactory
import org.ossreviewtoolkit.analyzer.PackageManager
import org.ossreviewtoolkit.model.Project
import org.ossreviewtoolkit.model.ProjectAnalyzerResult
import org.ossreviewtoolkit.model.config.AnalyzerConfiguration
import org.ossreviewtoolkit.model.config.RepositoryConfiguration
import org.ossreviewtoolkit.model.createAndLogIssue

class MyPackageManager(
    name: String,
    analysisRoot: File,
    analyzerConfig: AnalyzerConfiguration,
    repoConfig: RepositoryConfiguration
) : PackageManager(name, analysisRoot, analyzerConfig, repoConfig) {
    class Factory : AbstractPackageManagerFactory<MyPackageManager>("MyPackageManager") {
        override val globsForDefinitionFiles = listOf("MyPackageManager.DefinitionFile")

        override fun create(
            analysisRoot: File,
            analyzerConfig: AnalyzerConfiguration,
            repoConfig: RepositoryConfiguration
        ) = MyPackageManager(type, analysisRoot, analyzerConfig, repoConfig)
    }

    override fun resolveDependencies(definitionFile: File, labels: Map<String, String>): List<ProjectAnalyzerResult> {
        val issue = createAndLogIssue(
            source = managerName,
            message = "Not yet implemented."
        )

        val result = ProjectAnalyzerResult(
            project = Project.EMPTY,
            packages = emptySet(),
            issues = listOf(issue)
        )

        return listOf(result)
    }
}
