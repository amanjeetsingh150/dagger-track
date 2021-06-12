package me.amanjeet.daggertrack.transform

import javassist.CtClass
import me.amanjeet.daggertrack.DaggerComponentsVisitorImpl
import me.amanjeet.daggertrack.copyCtClasses
import me.amanjeet.daggertrack.filterDaggerComponents
import java.io.File

internal class DaggerAndroidClassTransform : DaggerTrackClassTransform {

    override fun handleClassTransformation(allCtClasses: List<CtClass>, outputDir: File) {
        val daggerComponentCtClasses = mutableListOf<CtClass>()
        daggerComponentCtClasses += allCtClasses.filterDaggerComponents()
        val daggerComponentsVisitor = DaggerComponentsVisitorImpl()
        daggerComponentCtClasses.forEach {
            daggerComponentsVisitor.visit(it)
        }
        daggerComponentCtClasses.copyCtClasses(outputDir.canonicalPath)
        (allCtClasses - daggerComponentCtClasses).copyCtClasses(outputDir.canonicalPath)
    }
}