package com.droidsingh.daggertrack.utils

import javassist.CtClass
import javassist.bytecode.AnnotationsAttribute
import javassist.bytecode.annotation.Annotation

internal fun CtClass.addSubcomponentAnnotation() {
    val constPool = this.classFile.constPool
    val annotationsAttribute = AnnotationsAttribute(
        constPool,
        AnnotationsAttribute.visibleTag
    )
    val annotation = Annotation("dagger.Subcomponent", constPool)
    annotationsAttribute.addAnnotation(annotation)
    this.classFile.addAttribute(annotationsAttribute)
}
