package me.amanjeet.daggertrack.transform

import com.android.build.gradle.BaseExtension
import javassist.ClassPool
import javassist.CtClass
import me.amanjeet.daggertrack.DaggerComponentsVisitorImpl
import java.io.File

class DaggerTrackClassTransform(
    allInputs: List<File>,
    private val sourceRootOutputDir: File,
    private val isDaggerHiltProject: Boolean,
    private val copyNonTransformed: Boolean,
    baseExtension: BaseExtension
) {

    private val classPool: ClassPool = ClassPool(true).also { pool ->
        allInputs.forEach {
            pool.appendClassPath(it.path)
        }
    }

    private val daggerComponentsVisitor by lazy { DaggerComponentsVisitorImpl() }

    init {
        sourceRootOutputDir.mkdirs()
        val androidJar = "${baseExtension.sdkDirectory.absolutePath}/platforms" +
                "/${baseExtension.compileSdkVersion}/android.jar"
        classPool.insertClassPath(androidJar)
    }

    fun transformFile(inputFile: File) {
        val clazz = inputFile.inputStream().use { classPool.makeClass(it, false) }
        transformClassToOutput(clazz)
    }

    private fun transformClassToOutput(clazz: CtClass) {
        val transformed = transformClass(clazz)
        if (transformed || copyNonTransformed) {
            clazz.writeFile(sourceRootOutputDir.path)
        }
    }

    private fun transformClass(clazz: CtClass): Boolean {
        if (isDaggerHiltProject) {
            val isDaggerHiltSingletonComponent =
                clazz.superclass.name.contains(Regex(".*HiltComponents\\$[a-zA-Z]+C\$"))
            if (!isDaggerHiltSingletonComponent) return false

            transformDaggerHiltComponentClass(clazz)
            return true
        } else {
            return transformDaggerAndroidComponentClass(clazz)
        }
    }

    private fun transformDaggerAndroidComponentClass(clazz: CtClass): Boolean {
        val isDaggerComponent = clazz.interfaces.any { interfaceClass ->
            interfaceClass.hasAnnotation("dagger.Component") ||
                    interfaceClass.hasAnnotation("dagger.Subcomponent")
        }
        if (!isDaggerComponent) return false

        daggerComponentsVisitor.visitDaggerAndroidComponents(clazz)
        return true
    }

    private fun transformDaggerHiltComponentClass(clazz: CtClass) {
        daggerComponentsVisitor.visitDaggerHiltComponents(clazz)
    }
}