package me.amanjeet.daggertrack.transform

import javassist.CtClass
import java.io.File

internal interface DaggerTrackClassTransform {
    fun handleClassTransformation(allCtClasses: List<CtClass>, outputDir: File)
}