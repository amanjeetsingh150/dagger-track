package me.amanjeet.daggertrack.utils

import java.io.File
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.rules.TemporaryFolder

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
    private var additionalTasks = mutableListOf<String>()

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

    // Executes a Gradle build and expects it to fail.
    fun buildAndFail(): Result {
        setupFiles()
        return Result(tempFolder.root, createRunner().buildAndFail())
    }

    fun buildAndFailJavaLibrary(): Result {
        tempFolder.newFile("build.gradle").apply {
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

    private fun setupFiles() {
        writeBuildFile()
        writeGradleProperties()
        writeAndroidManifest()
    }

    private fun writeBuildFile() {
        buildFile?.delete()
        buildFile = tempFolder.newFile("build.gradle").apply {
            writeText(
                """
        buildscript {
          repositories {
            google()
            maven {
                url 'https://s01.oss.sonatype.org/content/repositories/snapshots'
            }
          }
          dependencies {
            classpath 'com.android.tools.build:gradle:4.2.0'
            classpath "me.amanjeet.daggertrack:dagger-track:1.0.6-SNAPSHOT"
          }
        }
        plugins {
          id 'com.android.application'
          id 'me.amanjeet.daggertrack'
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
            maven {
                url 'https://s01.oss.sonatype.org/content/repositories/snapshots'
            }
          }
        }
        daggerTrack {
            applyFor = ["debug"]
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
        .withArguments(listOf("--stacktrace", "assembleDebug") + additionalTasks)
        .withPluginClasspath()
        .forwardOutput()

    // data class representing a Gradle Test run result.
    data class Result(
        private val projectRoot: File,
        private val buildResult: BuildResult
    ) {
        // Gets the full build output.
        fun getOutput(): String = buildResult.output
    }
}