package me.amanjeet.daggertrack

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.BaseExtension
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import javassist.ClassPool
import org.junit.Before
import org.junit.Test
import java.io.File

internal class ClassPoolFactoryTest {

    private val transformInvocation = mock<TransformInvocation>()
    private val transformInput = mock<TransformInput>()
    private val directoryInput = mock<DirectoryInput>()
    private val jarInput = mock<JarInput>()
    private val jarFile = mock<File>()
    private val directoryInputFile = mock<File>()
    private val sdkDirectory = mock<File>()
    private val classPool = mock<ClassPool>()
    private val android = mock<BaseExtension>()
    private val transformInputCollection = arrayListOf(transformInput)
    private val directoryInputCollection = arrayListOf(directoryInput)
    private val jarInputCollection = arrayListOf(jarInput)

    @Before
    fun setup() {
        whenever(sdkDirectory.absolutePath).thenReturn("/directory/androidSdk")
        whenever(android.sdkDirectory).thenReturn(sdkDirectory)
        whenever(android.compileSdkVersion).thenReturn("28")
    }

    @Test
    fun `it builds class pool of external libraries`() {
        // given
        val externalJarPath = "/jars/myExternalJar.jar"
        val externalClassDirectoryPath = "/directory/MyExternalClass"
        whenever(jarFile.absolutePath).thenReturn(externalJarPath)
        whenever(directoryInputFile.absolutePath).thenReturn(externalClassDirectoryPath)
        whenever(directoryInput.file).thenReturn(directoryInputFile)
        whenever(jarInput.file).thenReturn(jarFile)
        whenever(transformInvocation.referencedInputs).thenReturn(transformInputCollection)
        whenever(transformInput.directoryInputs).thenReturn(directoryInputCollection)
        whenever(transformInput.jarInputs).thenReturn(jarInputCollection)
        val androidJarPath = "${android.sdkDirectory.absolutePath}/platforms" +
                "/${android.compileSdkVersion}/android.jar"

        // when
        ClassPoolFactory(classPool).buildProjectClassPool(
            transformInvocation,
            android
        )

        // then
        verify(classPool).insertClassPath(androidJarPath)
        verify(classPool).insertClassPath(externalJarPath)
        verify(classPool).insertClassPath(externalClassDirectoryPath)
    }

    @Test
    fun `it includes project files inputs in class pool`() {
        // given
        val jarPath = "/jars/myInternalJar.jar"
        val directoryPath = "/directory/MyApplicationClass"
        whenever(jarFile.absolutePath).thenReturn(jarPath)
        whenever(directoryInputFile.absolutePath).thenReturn(directoryPath)
        whenever(directoryInput.file).thenReturn(directoryInputFile)
        whenever(jarInput.file).thenReturn(jarFile)
        whenever(transformInvocation.inputs).thenReturn(transformInputCollection)
        whenever(transformInput.directoryInputs).thenReturn(directoryInputCollection)
        whenever(transformInput.jarInputs).thenReturn(jarInputCollection)

        // when
        ClassPoolFactory(classPool).buildProjectClassPool(
            transformInvocation,
            android
        )

        // then
        verify(classPool).insertClassPath(jarPath)
        verify(classPool).insertClassPath(directoryPath)
    }
}