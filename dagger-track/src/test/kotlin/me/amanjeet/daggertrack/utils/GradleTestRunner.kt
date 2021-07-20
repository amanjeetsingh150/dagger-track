package me.amanjeet.daggertrack.utils

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.rules.TemporaryFolder
import java.io.File

/**
 * Testing utility class that sets up a simple Android project.
 *
 * Inspired from: https://github.com/google/dagger/blob/master/java/dagger/hilt/android/plugin/src/test/kotlin/GradleTestRunner.kt
 */
class GradleTestRunner(private val tempFolder: TemporaryFolder) {

    private val dependencies = mutableListOf<String>()
    private val activities = mutableListOf<String>()
    private val additionalAndroidOptions = mutableListOf<String>()
    private var appClassName: String? = null
    private var buildFile: File? = null
    private var gradlePropertiesFile: File? = null
    private var manifestFile: File? = null

    init {
        tempFolder.newFolder("src", "main", "java", "minimal")
        tempFolder.newFolder("src", "test", "java", "minimal")
        tempFolder.newFolder("src", "main", "res")
    }

    // Adds project dependencies, e.g. "implementation <group>:<id>:<version>"
    fun addDependencies(vararg deps: String) {
        dependencies.addAll(deps)
    }

    // Adds an <activity> tag in the project's Android Manifest, e.g. "<activity name=".Foo"/>
    fun addActivities(vararg activityElements: String) {
        activities.addAll(activityElements)
    }

    // Adds a source file to the project. The source path is relative to 'src/main/java'.
    fun addSrc(srcPath: String, srcContent: String): File {
        File(
            tempFolder.root,
            "src/main/java/${srcPath.substringBeforeLast(File.separator)}"
        ).mkdirs()
        return tempFolder.newFile("/src/main/java/$srcPath").apply { writeText(srcContent) }
    }

    fun setAppClassName(name: String) {
        appClassName = name
    }

    // Executes a Gradle builds and expects it to succeed.
    fun build(shouldIntegrateDaggerTrack: Boolean = true): Result {
        setupFiles(shouldIntegrateDaggerTrack)
        return Result(tempFolder.root, createRunner().build())
    }

    // Executes a Gradle build and expects it to fail.
    fun buildAndFail(shouldIntegrateDaggerTrack: Boolean = true): Result {
        setupFiles(shouldIntegrateDaggerTrack)
        return Result(tempFolder.root, createRunner().buildAndFail())
    }

    fun buildAndFailJavaLibrary(): Result {
        buildFile?.delete()
        buildFile = tempFolder.newFile("build.gradle").apply {
            writeText(
                """
                    | buildscript {
                    |     repositories {
                    |         google()
                    |         mavenCentral()
                    |         maven {
                    |            url 'https://s01.oss.sonatype.org/content/repositories/snapshots'
                    |         }
                    |     }
                    |     dependencies {
                    |         classpath "com.android.tools.build:gradle:4.2.1"
                    |         classpath "me.amanjeet.daggertrack:dagger-track:1.0.6-SNAPSHOT"
                    |     }
                    | }
                    |
                    | plugins {
                    |     id 'java-library'
                    |     id 'me.amanjeet.daggertrack'
                    | }
                    |
                """.trimMargin()
            )
        }
        return Result(tempFolder.root, createRunner().buildAndFail())
    }

    private fun setupFiles(shouldIntegrateDaggerTrack: Boolean) {
        writeBuildFile(shouldIntegrateDaggerTrack)
        writeGradleProperties()
        writeAndroidManifest()
    }

    private fun writeBuildFile(shouldIntegrateDaggerTrack: Boolean) {
        buildFile?.delete()
        buildFile = tempFolder.newFile("build.gradle").apply {
            writeText(
                """
                    buildscript {
                      repositories {
                        google()
                        mavenCentral()
                        maven {
                            url 'https://s01.oss.sonatype.org/content/repositories/snapshots'
                        }
                      }
                      dependencies {
                        classpath 'com.android.tools.build:gradle:4.2.0'
                        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.10"
                        ${if (shouldIntegrateDaggerTrack) "classpath \"me.amanjeet.daggertrack:dagger-track:1.0.6-SNAPSHOT\"" else "" }
                      }
                    }
                    plugins {
                      id 'com.android.application'
                      ${if (shouldIntegrateDaggerTrack) "id 'me.amanjeet.daggertrack'" else ""}
                    }
                    android {
                      compileSdkVersion 30
                      buildToolsVersion "30.0.2"
                      defaultConfig {
                        applicationId "plugin.test"
                        minSdkVersion 21
                        targetSdkVersion 30
                      }
                      compileOptions {
                          sourceCompatibility 1.8
                          targetCompatibility 1.8
                      }
                      ${additionalAndroidOptions.joinToString(separator = "\n")}
                    }
                    allprojects {
                      repositories {
                        google()
                        mavenCentral()
                        maven {
                            url 'https://s01.oss.sonatype.org/content/repositories/snapshots'
                        }
                      }
                    }
                    ${
                        if (shouldIntegrateDaggerTrack) {
                            """
                                daggerTrack {
                                   applyFor = ["debug"]
                                }
                            """.trimIndent()
                        } else {
                            ""
                        }
                    }
                    dependencies {
                      ${dependencies.joinToString(separator = "\n")}
                    }
                """.trimIndent()
            )
        }
    }

    private fun writeGradleProperties() {
        gradlePropertiesFile?.delete()
        gradlePropertiesFile = tempFolder.newFile("gradle.properties").apply {
            writeText(
                """
        android.useAndroidX=true
        """.trimIndent()
            )
        }
    }

    private fun writeAndroidManifest() {
        manifestFile?.delete()
        manifestFile = tempFolder.newFile("/src/main/AndroidManifest.xml").apply {
            writeText(
                """
        <?xml version="1.0" encoding="utf-8"?>
        <manifest xmlns:android="http://schemas.android.com/apk/res/android" package="minimal">
            <application
                android:name="${appClassName ?: "android.app.Application"}"
                android:theme="@style/Theme.AppCompat.Light.DarkActionBar">
                ${activities.joinToString(separator = "\n")}
            </application>
        </manifest>
        """.trimIndent()
            )
        }
    }

    private fun createRunner() = GradleRunner.create()
        .withProjectDir(tempFolder.root)
        .withArguments(listOf("--stacktrace", "assembleDebug"))
        .withPluginClasspath()
        .forwardOutput()

    // data class representing a Gradle Test run result.
    data class Result(
        private val projectRoot: File,
        private val buildResult: BuildResult
    ) {
        // Finds a task by name.
        fun getTask(name: String) = buildResult.task(name) ?: error("Task '$name' not found.")

        // Gets the full build output.
        fun getOutput(): String = buildResult.output

        // Finds a transformed file. The srcFilePath is relative to the app's package.
        fun getTransformedFile(srcFilePath: String): File {
            val parentDir =
                File(projectRoot, "build/intermediates/transforms/DAGGER_TRACK/debug/0")
            return File(parentDir, srcFilePath).also {
                if (!it.exists()) {
                    error("Unable to find transformed class ${it.path}")
                }
            }
        }

        fun getClassFile(srcPath: String): File {
            val parentDir = File(projectRoot, "build/intermediates/javac/debug/classes")
            return File(parentDir, srcPath).also {
                if (!it.exists()) {
                    error("Unable to find class file for $srcPath")
                }
            }
        }
    }
}