package me.amanjeet.daggertrack.transform

import javassist.CtClass
import me.amanjeet.daggertrack.DaggerComponentsVisitorImpl
import me.amanjeet.daggertrack.copyCtClasses
import me.amanjeet.daggertrack.filterDaggerHiltComponents
import java.io.File

internal class DaggerHiltClassTransform : DaggerTrackClassTransform {

    override fun handleClassTransformation(allCtClasses: List<CtClass>, outputDir: File) {
        val daggerComponentCtClasses = mutableListOf<CtClass>()
        daggerComponentCtClasses += allCtClasses.filterDaggerHiltComponents()
        val daggerComponentsVisitor = DaggerComponentsVisitorImpl()
        daggerComponentCtClasses.forEach {
            daggerComponentsVisitor.visitDaggerHiltComponents(it)
        }
        daggerComponentCtClasses.copyCtClasses(outputDir.canonicalPath)
        (allCtClasses - daggerComponentCtClasses).copyCtClasses(outputDir.canonicalPath)
    }
}