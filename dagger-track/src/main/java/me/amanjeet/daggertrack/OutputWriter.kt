package me.amanjeet.daggertrack

import com.android.build.api.transform.Format
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import javassist.CtClass

/**
 * Ensures all jar are also copied, containing R classes. From Android AGP 3.6.0 R classes are
 * packaged inside jar.
 *
 * @param transformInvocation to extract jar inputs
 */
fun Transform.copyAllJars(transformInvocation: TransformInvocation) {
    transformInvocation.inputs.forEach { transformInput ->
        // Ensure JARs are copied as well:
        transformInput.jarInputs.forEach {
            it.file.copyTo(
                transformInvocation.outputProvider.getContentLocation(
                    it.name,
                    inputTypes,
                    scopes,
                    Format.JAR
                ),
                overwrite = true
            )
        }
    }
}

/**
 * Copy the list of [CtClass]
 *
 * @param outputDir output path for class files
 */
fun List<CtClass>.copyCtClasses(outputDir: String) {
    forEach { clazz -> clazz.writeFile(outputDir) }
}