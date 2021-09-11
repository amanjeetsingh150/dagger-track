package me.amanjeet.daggertrack.utils

import javassist.ClassPool
import javassist.CtClass
import javassist.expr.ExprEditor
import javassist.expr.MethodCall
import java.io.File

fun ClassPool.getCtClassFromName(
    result: GradleTestRunner.Result,
    className: String
): CtClass {
    val daggerAppComponentClassFile = result.getTransformedFile(className)
    return makeClass(daggerAppComponentClassFile.inputStream())
}

fun CtClass.getMethodCalls(methodName: String): List<String> {
    val methodCalls = mutableListOf<String>()
    declaredMethods.first { it.name == methodName }
        .instrument(object : ExprEditor() {
            override fun edit(methodCall: MethodCall) {
                super.edit(methodCall)
                methodCalls.add(methodCall.methodName)
            }
        })
    return methodCalls.toList()
}