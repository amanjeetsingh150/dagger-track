package me.amanjeet.daggertrack

import com.google.common.truth.Truth.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class DaggerTrackPluginTest {

    @get:Rule
    val testProjectDir = TemporaryFolder()

    private lateinit var gradleRunner: GradleRunner

    @Before
    fun setUp() {
        gradleRunner = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withPluginClasspath()
    }

    @Test
    fun `dagger track fails if it is not applied to android app or library`() {
        // given
        projectFile(
            path = "build.gradle",
            content = """
              | buildscript {
              |     repositories {
              |         google()
              |         mavenCentral()
              |         maven {
              |            url 'https://s01.oss.sonatype.org/content/repositories/snapshots/'
              |         }
              |     }
              |     dependencies {
              |         classpath "com.android.tools.build:gradle:4.2.1"
              |         classpath "me.amanjeet.daggertrack:dagger-track:1.0.5-SNAPSHOT"
              |     }
              | }
              | 
              | plugins {
              |     id 'java-library'
              |     id 'me.amanjeet.daggertrack'
              | }
              | 
              | 
            """.trimMargin()
        )

        // when
        val result = gradleRunner
            .buildAndFail()

        // then
        assertThat(
            result.output.contains(
                "'com.android.application' or 'com.android.library' plugin required"
            )
        ).isTrue()
    }

    private fun projectFile(path: String, content: String): File {
        val root = testProjectDir.root
        return File(root, path).apply {
            parentFile?.let { if (!it.exists()) it.mkdirs() }
            if (exists()) delete()
            createNewFile()
            writeText(content)
        }
    }
}