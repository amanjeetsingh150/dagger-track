package me.amanjeet.daggertrack

import javassist.CtClass

internal interface DaggerComponentsVisitor {
    fun visitDaggerAndroidComponents(daggerComponent: CtClass)
    fun visitDaggerHiltComponents(daggerComponent: CtClass)
}
