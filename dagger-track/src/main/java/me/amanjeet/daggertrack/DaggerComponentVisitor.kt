package me.amanjeet.daggertrack

import javassist.CtClass

internal interface DaggerComponentsVisitor {
    fun visit(daggerComponent: CtClass)
}
