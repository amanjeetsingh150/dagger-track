package me.amanjeet.daggertrack

import com.android.SdkConstants.DOT_CLASS
import com.android.build.gradle.BaseExtension
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import javassist.ClassPool
import org.junit.Before
import org.junit.Test
import java.io.File

internal class ClassPoolFactoryTest {

    private val defaultClassPool = ClassPool.getDefault()
    private val sdkDirectory = mock<File>()
    private val android = mock<BaseExtension>()

    @Before
    fun setup() {
        whenever(sdkDirectory.absolutePath).thenReturn("./src/test/resources/fake-android-sdk")
        whenever(android.sdkDirectory).thenReturn(sdkDirectory)
        whenever(android.compileSdkVersion).thenReturn("android-28")
    }

    @Test
    fun `it builds class pool of external libraries`() {
        // given
        val classFileInputSequence = File("./src/test/resources")
            .walkTopDown()
            .map { it }
            .filter { it.absolutePath.endsWith(DOT_CLASS) }

        // when
        val classPool = ClassPoolFactory(defaultClassPool).buildProjectClassPool(
            classFileInputSequence.toList(),
            android
        )

        // then, class file from dummy android jar & external class file exist in classpool
        val classFromJarFile = classPool.get("me.amanjeet.daggertrack.ClassPoolFactory")
        val applicationComponent = classPool.get("me.amanjeet.daggertrack.di.components.ApplicationComponent")
        assertThat(classFromJarFile.simpleName).isEqualTo("ClassPoolFactory")
        assertThat(applicationComponent.simpleName).isEqualTo("ApplicationComponent")
    }
}