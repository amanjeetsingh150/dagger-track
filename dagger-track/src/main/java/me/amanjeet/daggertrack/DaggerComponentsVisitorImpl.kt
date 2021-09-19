package me.amanjeet.daggertrack

import javassist.CtClass
import javassist.CtMethod
import java.lang.reflect.Modifier

/**
 * Visits dagger components and subcomponents to add clock logs.
 */
internal class DaggerComponentsVisitorImpl : DaggerComponentsVisitor {

    override fun visitDaggerAndroidComponents(daggerComponent: CtClass) {
        val methodPredicate: (ctMethod: CtMethod) -> Boolean = { it.name == "inject" }
        setComponentTracking(daggerComponent, methodPredicate)
        daggerComponent.filterSubcomponents()
            .forEach { setComponentTracking(it, methodPredicate) }
    }

    override fun visitDaggerHiltComponents(daggerComponent: CtClass) {
        val methodPredicate: (ctMethod: CtMethod) -> Boolean = {
            it.name.contains("inject") && it.modifiers == Modifier.PUBLIC
        }
        setComponentTracking(daggerComponent, methodPredicate)
    }

    private fun setComponentTracking(
        component: CtClass,
        methodPredicate: (ctMethod: CtMethod) -> Boolean
    ) {
        defrostCtClass(component)
        component.declaredMethods.filter { methodPredicate(it) }.forEach { injectMethod ->
            val injectParam = injectMethod.parameterTypes.first().name
            setTrackingLogs(injectMethod, injectParam)
        }
    }

    private fun setTrackingLogs(injectMethod: CtMethod, injectParam: String?) {
        injectMethod.insertBefore("me.amanjeet.daggertrack.DaggerTrack.INSTANCE.onInjectionStart();")
        injectMethod.insertAfter("me.amanjeet.daggertrack.DaggerTrack.INSTANCE.onInjectionEnd(\"${injectParam.toString()}\");")
    }

    private fun defrostCtClass(ctClass: CtClass) {
        if (ctClass.isFrozen) {
            ctClass.defrost()
        }
    }
}