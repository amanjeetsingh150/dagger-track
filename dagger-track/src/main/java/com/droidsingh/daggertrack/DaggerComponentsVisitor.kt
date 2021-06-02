package com.droidsingh.daggertrack

import javassist.CtClass
import javassist.CtMethod

internal interface DaggerComponentsVisitor {
    fun visit(daggerComponent: CtClass)
}

/**
 * Visits dagger components and subcomponents to add clock logs.
 */
internal class DaggerComponentsVisitorImpl : DaggerComponentsVisitor {
    override fun visit(daggerComponent: CtClass) {
        setComponentTracking(daggerComponent)
        daggerComponent.filterSubcomponents()
            .forEach { setComponentTracking(it) }
    }

    private fun setComponentTracking(component: CtClass) {
        defrostCtClass(component)
        component.declaredMethods.filter { it.name == "inject" }.forEach { injectMethod ->
            val injectParam = injectMethod.parameterTypes.first().name
            setTrackingLogs(injectMethod, injectParam)
        }
    }

    private fun setTrackingLogs(injectMethod: CtMethod, injectParam: String?) {
        injectMethod.addLocalVariable("initialTime", CtClass.longType)
        injectMethod.addLocalVariable("initialCpuTime", CtClass.longType)
        injectMethod.insertBefore(
            """
                    long initialTime = com.droidsingh.daggertrack.DaggerTrackClocks.getUptimeMillis();
                    long initialCpuTime = com.droidsingh.daggertrack.DaggerTrackClocks.getCpuTimeMillis();
                """.trimIndent()
        )
        injectMethod.addLocalVariable("endTime", CtClass.longType)
        injectMethod.addLocalVariable("endCpuTime", CtClass.longType)
        injectMethod.insertAfter(
            """
                    long endTime = com.droidsingh.daggertrack.DaggerTrackClocks.getUptimeMillis();
                    long endCpuTime = com.droidsingh.daggertrack.DaggerTrackClocks.getCpuTimeMillis();
                    android.util.Log.d("DaggerTrack","Total time of ${injectParam}: " + (endTime - initialTime));
                    android.util.Log.d("DaggerTrack","Total On CPU time of ${injectParam}: " + (endCpuTime - initialCpuTime));
                    android.util.Log.d("DaggerTrack","Total Off CPU time of ${injectParam}: " + ((endTime - initialTime) - (endCpuTime - initialCpuTime)));
                """.trimIndent()
        )
    }

    private fun defrostCtClass(ctClass: CtClass) {
        if (ctClass.isFrozen) {
            ctClass.defrost()
        }
    }
}