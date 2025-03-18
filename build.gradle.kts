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

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

import io.gitlab.arturbosch.detekt.Detekt

import org.jetbrains.gradle.ext.GradleTask
import org.jetbrains.gradle.ext.JarApplication
import org.jetbrains.gradle.ext.runConfigurations
import org.jetbrains.gradle.ext.settings
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val javaLanguageVersion: String by project

plugins {
    // Apply core plugins.
    distribution
    `java-library`

    // Apply third-party plugins.
    alias(libs.plugins.detekt)
    alias(libs.plugins.ideaExt)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.ksp)
    alias(libs.plugins.shadow)
}

repositories {
    mavenCentral()
}

configurations {
    // Create an extended configuration with the analyzer CLI to run the plugin from an IDEA run configuration.
    resolvable("analyzerCliClasspath").get().extendsFrom(project.configurations["runtimeClasspath"])
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(javaLanguageVersion)
        vendor = JvmVendorSpec.ADOPTIUM
    }
}

val maxKotlinJvmTarget = runCatching { JvmTarget.fromTarget(javaLanguageVersion) }
    .getOrDefault(enumValues<JvmTarget>().max())

tasks.withType<KotlinCompile> {
    compilerOptions {
        allWarningsAsErrors = true
        jvmTarget = maxKotlinJvmTarget
    }
}

val shadowJar = tasks.named<ShadowJar>("shadowJar") {
    archiveClassifier = "analyzer-cli"

    // Extend the "runtimeClasspath", which is the configuration that "Shadow" uses by default, with the classpath for
    // the ORT main CLI and the analyzer command.
    configurations = listOf(project.configurations["analyzerCliClasspath"])

    manifest.attributes["Main-Class"] = "org.ossreviewtoolkit.cli.OrtMainKt"
}

idea {
    project {
        settings {
            runConfigurations {
                create<JarApplication>("ORT Analyzer CLI Template") {
                    beforeRun {
                        create("Build Shadow JAR", GradleTask::class) {
                            task = shadowJar.get()
                        }
                    }

                    jarPath = shadowJar.get().outputs.files.singleFile.path
                    programParameters = "-Port.forceOverwrite=true --info analyze -i <input-dir> -o <output-dir>"
                }
            }
        }
    }
}

distributions {
    main {
        contents {
            from(tasks["jar"])
            from(configurations["runtimeClasspath"])
        }
    }
}

testing {
    suites {
        withType<JvmTestSuite>().configureEach {
            useJUnitJupiter()

            dependencies {
                implementation(libs.kotestAssertionsCore)
                implementation(libs.kotestRunnerJunit5)

                runtimeOnly(libs.log4jApiToSlf4j)
                runtimeOnly(libs.logbackClassic)
            }
        }

        register<JvmTestSuite>("funTest") {
            dependencies {
                implementation(project())
            }
        }
    }
}

// Associate the "funTest" compilation with the "main" compilation to be able to access "internal" objects from
// functional tests.
kotlin.target.compilations.apply {
    getByName("funTest").associateWith(getByName(KotlinCompilation.MAIN_COMPILATION_NAME))
}

dependencies {
    compileOnlyApi(libs.ortDownloader)
    compileOnlyApi(libs.ortPluginApi)
    compileOnlyApi(libs.ortAnalyzer)
    compileOnlyApi(libs.ortModel)

    detektPlugins(libs.detektFormatting)
    detektPlugins(libs.ortDetektRules)

    implementation(libs.log4jApiKotlin)

    ksp(libs.ortAnalyzer)
    ksp(libs.ortPluginCompiler)

    "analyzerCliClasspath"(libs.ortAnalyzerCommand)
    "analyzerCliClasspath"(libs.ortCli)

    "funTestImplementation"(libs.ortTestUtils)
    "funTestImplementation"(variantOf(libs.ortAnalyzer) { classifier("test-fixtures") })

    "funTestRuntimeOnly"(libs.ortAnalyzer)
}

detekt {
    config.from(files(".detekt.yml"))
    buildUponDefaultConfig = true
}

tasks.withType<Detekt>().configureEach {
     jvmTarget = maxKotlinJvmTarget.target

    reports {
        xml.required.set(false)
        html.required.set(false)
        txt.required.set(false)
        sarif.required.set(true)
    }
}
