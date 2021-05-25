package com.droidsingh.daggertrack

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import javassist.ClassPool
import org.junit.Test
import java.io.File

internal class ClassPoolManagerTest {

    @Test
    fun `it builds class pool of external libraries`() {
        // given
        val transformInvocation = mock<TransformInvocation>()
        val transformInput = mock<TransformInput>()
        val directoryInput = mock<DirectoryInput>()
        val jarInput = mock<JarInput>()
        val jarFile = mock<File>()
        val directoryInputFile = mock<File>()
        val classPool = mock<ClassPool>()
        val transformInputCollection = arrayListOf(transformInput)
        val directoryInputCollection = arrayListOf(directoryInput)
        val jarInputCollection = arrayListOf(jarInput)
        val externalJarPath = "/jars/myExternalJar.jar"
        val externalClassDirectoryPath = "/directory/MyExternalClass"

        whenever(jarFile.absolutePath).thenReturn(externalJarPath)
        whenever(directoryInputFile.absolutePath).thenReturn(externalClassDirectoryPath)
        whenever(directoryInput.file).thenReturn(directoryInputFile)
        whenever(jarInput.file).thenReturn(jarFile)
        whenever(transformInvocation.referencedInputs).thenReturn(transformInputCollection)
        whenever(transformInput.directoryInputs).thenReturn(directoryInputCollection)
        whenever(transformInput.jarInputs).thenReturn(jarInputCollection)

        // when
        ClassPoolManager(classPool).buildProjectClassPool(
            transformInvocation
        )

        // then
        verify(classPool).insertClassPath(externalJarPath)
        verify(classPool).insertClassPath(externalClassDirectoryPath)
    }
}