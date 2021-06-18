package me.amanjeet.daggertrack

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.BaseExtension
import javassist.ClassPool
import javassist.NotFoundException
import me.amanjeet.daggertrack.DaggerTrackPlugin.DaggerTrackExtension
import me.amanjeet.daggertrack.transform.DaggerAndroidClassTransform
import me.amanjeet.daggertrack.transform.DaggerHiltClassTransform
import org.gradle.api.GradleException
import org.gradle.api.Project

internal class DaggerTrackTransform(
    private val project: Project,
    private val android: BaseExtension,
    private val daggerTrackExtension: DaggerTrackExtension
) : Transform() {

    private val logger = project.logger

    companion object {
        private const val TRANSFORM_NAME = "DAGGER_TRACK"
        private const val DAGGER_HILT_PLUGIN = "dagger.hilt.android.plugin"
    }

    override fun getName() = TRANSFORM_NAME

    override fun getInputTypes() = setOf(QualifiedContent.DefaultContentType.CLASSES)

    override fun getScopes() = mutableSetOf(QualifiedContent.Scope.PROJECT)

    override fun isIncremental() = false

    override fun getReferencedScopes(): MutableSet<in QualifiedContent.Scope> =
        mutableSetOf(
            QualifiedContent.Scope.EXTERNAL_LIBRARIES,
            QualifiedContent.Scope.SUB_PROJECTS
        )

    override fun transform(transformInvocation: TransformInvocation) {
        super.transform(transformInvocation)
        if (daggerTrackExtension.applyFor?.isEmpty() != false) {
            throw GradleException("No variants configured for Dagger Track transform plugin")
        }
        val variantName = transformInvocation.context.variantName
        val outputDir = transformInvocation.outputProvider.getContentLocation(
            name,
            outputTypes,
            scopes,
            Format.DIRECTORY
        )
        transformInvocation.outputProvider.deleteAll()
        val defaultClassPool = ClassPool.getDefault()
        val classPoolFactory = ClassPoolFactory(defaultClassPool)
        val classPool = classPoolFactory.buildProjectClassPool(
            transformInvocation,
            android
        )
        val shouldApplyTransform = daggerTrackExtension.applyFor?.find {
            variantName.endsWith(it, true)
        } != null
        val allCtClasses = classPool.mapToCtClassList(transformInvocation)
        if (shouldApplyTransform) {
            validateDaggerClocks(classPool)
            val isDaggerHiltProject = isDaggerHiltProject()
            if (isDaggerHiltProject) {
                val daggerHiltClassTransform = DaggerHiltClassTransform()
                daggerHiltClassTransform.handleClassTransformation(allCtClasses, outputDir)
            } else {
                val daggerAndroidClassTransform = DaggerAndroidClassTransform()
                daggerAndroidClassTransform.handleClassTransformation(allCtClasses, outputDir)
            }
        }
        copyAllJars(transformInvocation)
    }

    private fun validateDaggerClocks(classPool: ClassPool) {
        try {
            classPool.get("me.amanjeet.daggertrack.DaggerTrackClocks")
        } catch (notFoundException: NotFoundException) {
            throw GradleException("\"dagger-track-clocks\" dependency needed for dagger-track plugin")
        }
    }

    private fun isDaggerHiltProject(): Boolean {
        return project.plugins.hasPlugin(DAGGER_HILT_PLUGIN)
    }
}