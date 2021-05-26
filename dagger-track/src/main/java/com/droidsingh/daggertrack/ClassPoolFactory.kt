package com.droidsingh.daggertrack

import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.BaseExtension
import javassist.ClassPool

/**
 * A factory which helps creating [ClassPool] for the project
 *
 * @param classPool default class pool
 */
internal class ClassPoolFactory(private val classPool: ClassPool) {

    /**
     * Builds class pool of internal project files, external dependencies android jar from
     * sdk directory.
     *
     * @param transformInvocation TransformInvocation to copy jar and directory inputs
     * @param android BaseExtension for android to extract android.jar of compiledSdkVersion from
     * sdk directory
     */
    fun buildProjectClassPool(transformInvocation: TransformInvocation, android: BaseExtension): ClassPool {
        // External Deps
        transformInvocation.referencedInputs.forEach { transformInput ->
            transformInput.directoryInputs.forEach { classPool.insertClassPath(it.file.absolutePath) }
            transformInput.jarInputs.forEach { classPool.insertClassPath(it.file.absolutePath) }
        }
        // Project files
        transformInvocation.inputs.forEach { transformInput ->
            transformInput.directoryInputs.forEach{ classPool.insertClassPath(it.file.absolutePath) }
            transformInput.jarInputs.forEach { classPool.insertClassPath(it.file.absolutePath) }
        }
        // android jar from compiled sdk
        val androidJar = "${android.sdkDirectory.absolutePath}/platforms" +
                "/${android.compileSdkVersion}/android.jar"
        classPool.insertClassPath(androidJar)
        return classPool
    }
}