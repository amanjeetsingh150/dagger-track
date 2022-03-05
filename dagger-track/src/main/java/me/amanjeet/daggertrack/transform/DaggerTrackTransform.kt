package me.amanjeet.daggertrack.transform

import com.android.build.api.transform.*
import com.android.build.gradle.BaseExtension
import me.amanjeet.daggertrack.DaggerTrackPlugin.DaggerTrackExtension
import me.amanjeet.daggertrack.util.isClassFile
import me.amanjeet.daggertrack.util.toOutputFile
import org.gradle.api.Project
import java.io.File

class DaggerTrackTransform(
    private val project: Project,
    private val android: BaseExtension,
    private val daggerTrackExtension: DaggerTrackExtension
) : Transform() {

    override fun getName(): String = "DaggerTrackTransform"

    override fun getInputTypes() = setOf(QualifiedContent.DefaultContentType.CLASSES)

    override fun getScopes() = mutableSetOf(QualifiedContent.Scope.PROJECT)

    override fun isIncremental(): Boolean = true

    override fun getReferencedScopes(): MutableSet<in QualifiedContent.Scope> =
        mutableSetOf(
            QualifiedContent.Scope.EXTERNAL_LIBRARIES,
            QualifiedContent.Scope.SUB_PROJECTS
        )

    override fun transform(transformInvocation: TransformInvocation) {
        if (daggerTrackExtension.applyFor.isEmpty()) {
            throw error("No variants configured for dagger track")
        }
        if (!transformInvocation.isIncremental) {
            transformInvocation.outputProvider.deleteAll()
        }

        val isDaggerHiltProject = isDaggerHiltProject(project)
        transformInvocation.inputs.forEach { transformInput ->
            transformInput.jarInputs.forEach { jarInput ->
                val outputJar =
                    transformInvocation.outputProvider.getContentLocation(
                        jarInput.name,
                        jarInput.contentTypes,
                        jarInput.scopes,
                        Format.JAR
                    )

                if (transformInvocation.isIncremental) {
                    when (jarInput.status) {
                        Status.ADDED, Status.CHANGED -> copyJar(jarInput.file, outputJar)
                        Status.REMOVED -> outputJar.delete()
                        Status.NOTCHANGED -> {
                            // No need to transform
                        }
                        else -> {
                            error("Unknown status: ${jarInput.status}")
                        }
                    }
                } else {
                    copyJar(jarInput.file, outputJar)
                }
            }
            transformInput.directoryInputs.forEach { directoryInput ->
                val outputDir = transformInvocation.outputProvider.getContentLocation(
                    directoryInput.name,
                    directoryInput.contentTypes,
                    directoryInput.scopes,
                    Format.DIRECTORY
                )
                val classTransformer = createDaggerTrackTransform(
                    transformInvocation.inputs,
                    transformInvocation.referencedInputs,
                    outputDir,
                    isDaggerHiltProject
                )
                if (transformInvocation.isIncremental) {
                    directoryInput.changedFiles.forEach{ (file, status) ->
                        val outputFile = toOutputFile(outputDir, directoryInput.file, file)
                        when (status) {
                            Status.ADDED, Status.CHANGED ->
                                transformFile(file, outputFile.parentFile, classTransformer)
                            Status.REMOVED -> outputFile.delete()
                            Status.NOTCHANGED -> {
                                // No need to transform.
                            }
                            else -> {
                                error("Unknown status: $status")
                            }
                        }
                    }
                } else {
                    directoryInput.file.walkTopDown().forEach { file ->
                        val outputFile = toOutputFile(outputDir, directoryInput.file, file)
                        transformFile(file, outputFile.parentFile, classTransformer)
                    }
                }
            }
        }
    }

    private fun createDaggerTrackTransform(
        inputs: Collection<TransformInput>,
        referencedInputs: Collection<TransformInput>,
        outputDir: File,
        isDaggerHiltProject: Boolean
    ): DaggerTrackClassTransform {
        val classFiles = (inputs + referencedInputs).flatMap { input ->
            (input.directoryInputs + input.jarInputs).map { it.file }
        }
        return DaggerTrackClassTransform(
            allInputs = classFiles,
            sourceRootOutputDir = outputDir,
            isDaggerHiltProject = isDaggerHiltProject,
            copyNonTransformed = true,
            baseExtension = android
        )
    }

    private fun transformFile(
        inputFile: File,
        outputDir: File,
        transformer: DaggerTrackClassTransform
    ) {
        if (inputFile.isClassFile()) {
            transformer.transformFile(inputFile)
        } else if (inputFile.isFile) {
            // Copy all non .class files to the output.
            outputDir.mkdirs()
            val outputFile = File(outputDir, inputFile.name)
            inputFile.copyTo(target = outputFile, overwrite = true)
        }
    }

    private fun copyJar(inputJar: File, outputJar: File) {
        outputJar.parentFile?.mkdirs()
        inputJar.copyTo(target = outputJar, overwrite = true)
    }

    private fun isDaggerHiltProject(project: Project): Boolean {
        return project.plugins.hasPlugin(DAGGER_HILT_PLUGIN)
    }

    companion object {
        private const val DAGGER_HILT_PLUGIN = "dagger.hilt.android.plugin"
    }
}