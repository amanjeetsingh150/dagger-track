package me.amanjeet.daggertrack

import com.android.build.gradle.BaseExtension
import javassist.ClassPool
import java.io.File

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
     * @param projectInputs Project files including both direct and referenced of module
     * @param android BaseExtension for android to extract android.jar of compiledSdkVersion from
     * sdk directory
     */
    fun buildProjectClassPool(
        projectInputs: List<File>,
        android: BaseExtension
    ): ClassPool {
        projectInputs.forEach { classPool.insertClassPath(it.absolutePath) }
        // android jar from compiled sdk
        val androidJar = "${android.sdkDirectory.absolutePath}/platforms" +
                "/${android.compileSdkVersion}/android.jar"
        classPool.insertClassPath(androidJar)
        return classPool
    }
}