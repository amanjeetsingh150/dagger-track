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
        injectMethod.addLocalVariable("initialTime", CtClass.longType)
        injectMethod.addLocalVariable("initialCpuTime", CtClass.longType)
        injectMethod.insertBefore(
            """
                long initialTime = me.amanjeet.daggertrack.DaggerTrackClocks.getUptimeMillis();
                long initialCpuTime = me.amanjeet.daggertrack.DaggerTrackClocks.getCpuTimeMillis();
            """.trimIndent()
        )
        injectMethod.addLocalVariable("endTime", CtClass.longType)
        injectMethod.addLocalVariable("endCpuTime", CtClass.longType)
        injectMethod.insertAfter(
            """
                long endTime = me.amanjeet.daggertrack.DaggerTrackClocks.getUptimeMillis();
                long endCpuTime = me.amanjeet.daggertrack.DaggerTrackClocks.getCpuTimeMillis();
                android.util.Log.d("DaggerTrack","Total time of ${injectParam}: " + (endTime - initialTime) + "ms");
                android.util.Log.d("DaggerTrack","Total On CPU time of ${injectParam}: " + (endCpuTime - initialCpuTime) + "ms");
                android.util.Log.d("DaggerTrack","Total Off CPU time of ${injectParam}: " + ((endTime - initialTime) - (endCpuTime - initialCpuTime)) + "ms");
            """.trimIndent()
        )
    }

    private fun defrostCtClass(ctClass: CtClass) {
        if (ctClass.isFrozen) {
            ctClass.defrost()
        }
    }
}