package com.droidsingh.daggertrack

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import javassist.ClassPool
import org.junit.Test
import java.io.File

internal class ClassPoolTransformationsTest {

    @Test
    fun `it transforms the class pool to list of CtClass`() {
        // given
        val transformInvocation = mock<TransformInvocation>()
        val inputs = mock<TransformInput>()
        val directoryInput = mock<DirectoryInput>()
        val file = File("./src/test/resources")
        val classPool = ClassPool.getDefault()
        classPool.makeClass("com.droidsingh.daggertrack.HomeActivity")
        whenever(transformInvocation.inputs).thenReturn(arrayListOf(inputs))
        whenever(inputs.directoryInputs).thenReturn(arrayListOf(directoryInput))
        whenever(directoryInput.file).thenReturn(file)

        // when
        val ctClassList = classPool.mapToCtClassList(transformInvocation)

        // then
        val classNameList = ctClassList.map { it.name }
        assertThat(classNameList).contains("com.droidsingh.daggertrack.HomeActivity")
    }
}