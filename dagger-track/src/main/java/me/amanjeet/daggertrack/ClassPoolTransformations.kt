package me.amanjeet.daggertrack

import com.android.SdkConstants.DOT_CLASS
import com.android.build.api.transform.TransformInvocation
import javassist.ClassPool
import javassist.CtClass
import java.io.File

/**
 * Maps all classes in given [ClassPool] to list of [CtClass].
 */
internal fun ClassPool.mapToCtClassList(transformInvocation: TransformInvocation): List<CtClass> {
    val allClassFiles = mutableListOf<CtClass>()

    transformInvocation.inputs.forEach { inputs ->
        inputs.directoryInputs.forEach { directory ->
            val inputPath = directory.file.absolutePath
            directory.file.walkTopDown()
                .forEach {
                    if (it.absolutePath.endsWith(DOT_CLASS)) {
                        val fullyQualifiedClassName = it.absolutePath
                            .substring(
                                inputPath.length + 1,
                                it.absolutePath.length - DOT_CLASS.length
                            ).replace(File.separator, ".")
                        allClassFiles += get(fullyQualifiedClassName)
                    }
                }
        }
    }
    return allClassFiles.toList()
}