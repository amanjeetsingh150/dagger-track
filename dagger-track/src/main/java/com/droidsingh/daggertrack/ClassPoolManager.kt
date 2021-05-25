package com.droidsingh.daggertrack

import com.android.build.api.transform.TransformInvocation
import javassist.ClassPool

internal class ClassPoolManager(private val classPool:ClassPool) {

    fun buildProjectClassPool(transformInvocation: TransformInvocation): ClassPool {
        // External Deps
        transformInvocation.referencedInputs.forEach { transformInput ->
            transformInput.directoryInputs.forEach { classPool.insertClassPath(it.file.absolutePath) }
            transformInput.jarInputs.forEach { classPool.insertClassPath(it.file.absolutePath) }
        }
        return classPool
    }
}