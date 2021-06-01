package com.droidsingh.daggertrack

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.BaseExtension
import com.droidsingh.daggertrack.DaggerTrackPlugin.DaggerTrackExtension
import javassist.ClassPool
import javassist.CtClass
import javassist.NotFoundException
import org.gradle.api.GradleException
import org.gradle.api.Project

internal class DaggerTrackTransform(
    project: Project,
    private val android: BaseExtension,
    private val daggerTrackExtension: DaggerTrackExtension
) : Transform() {

    private val logger = project.logger

    companion object {
        private const val TRANSFORM_NAME = "DAGGER_TRACK"
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
        val daggerComponentCtClasses = mutableListOf<CtClass>()
        if (shouldApplyTransform) {
            validateDaggerClocks(classPool)
            daggerComponentCtClasses += allCtClasses.filterDaggerComponents()
            val daggerComponentsVisitor = DaggerComponentsVisitorImpl()
            daggerComponentCtClasses.forEach {
                daggerComponentsVisitor.visit(it)
            }
            daggerComponentCtClasses.copyCtClasses(outputDir.canonicalPath)
        }
        copyAllJars(transformInvocation)
        (allCtClasses - daggerComponentCtClasses).copyCtClasses(outputDir.canonicalPath)
    }

    private fun validateDaggerClocks(classPool: ClassPool) {
        try {
            classPool.get("com.droidsingh.daggertrack.DaggerTrackClocks")
        } catch (notFoundException: NotFoundException) {
            throw GradleException("\"dagger-track-clocks\" dependency needed for dagger-track plugin")
        }
    }
}