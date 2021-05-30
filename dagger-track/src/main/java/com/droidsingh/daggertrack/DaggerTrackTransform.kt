package com.droidsingh.daggertrack

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.BaseExtension
import javassist.ClassPool
import org.gradle.api.Project

internal class DaggerTrackTransform(
    project: Project,
    private val android: BaseExtension
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
        val allCtClasses = classPool.mapToCtClassList(transformInvocation)
        val daggerComponentCtClasses = allCtClasses.filterDaggerComponents()
        val daggerComponentsVisitor = DaggerComponentsVisitorImpl()
        daggerComponentCtClasses.forEach {
            daggerComponentsVisitor.visit(it)
        }
        copyAllJars(transformInvocation)
        daggerComponentCtClasses.copyCtClasses(outputDir.canonicalPath)
        (allCtClasses - daggerComponentCtClasses).copyCtClasses(outputDir.canonicalPath)
    }
}