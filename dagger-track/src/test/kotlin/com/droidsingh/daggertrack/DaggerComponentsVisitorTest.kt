package com.droidsingh.daggertrack

import com.droidsingh.daggertrack.utils.addSubcomponentAnnotation
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import org.junit.Test

internal class DaggerComponentsVisitorTest {

    private val classPool = ClassPool.getDefault()
    private val applicationComponent = prepareComponent(
        injectParam = "com.droidsingh.daggertrack.DaggerTrackApp",
        componentName = "com.droidsingh.daggertrack.DaggerTrackApplicationComponent"
    )
    private val activitySubcomponentImpl = prepareComponent(
        injectParam = "com.droidsingh.daggertrack.ui.HomeActivity",
        componentName = "com.droidsingh.daggertrack.DaggerTrackApplicationComponent.HomeActivitySubcomponentImpl"
    )
    private val fragmentSubcomponentImpl = prepareComponent(
        injectParam = "com.droidsingh.daggertrack.ui.HomeFragment",
        componentName = "com.droidsingh.daggertrack.DaggerTrackApplicationComponent" +
                ".HomeActivitySubcomponentImpl.HomeFragmentSubcomponentImpl"
    )

    @Test
    fun `it visits the components and their subcomponents and add tracking logs on inject`() {
        // given
        val activitySubcomponent = classPool.makeInterface(
            "com.droidsingh.daggertrack.di.components.HomeActivitySubcomponent"
        )
        activitySubcomponent.addSubcomponentAnnotation()
        val fragmentSubcomponent = classPool.makeInterface(
            "com.droidsingh.daggertrack.di.components.HomeFragmentSubcomponent"
        )
        fragmentSubcomponent.addSubcomponentAnnotation()
        whenever(applicationComponent.nestedClasses).thenReturn(arrayOf(activitySubcomponentImpl))
        whenever(activitySubcomponentImpl.nestedClasses).thenReturn(arrayOf(fragmentSubcomponentImpl))
        whenever(activitySubcomponentImpl.interfaces).thenReturn(arrayOf(activitySubcomponent))
        whenever(fragmentSubcomponentImpl.interfaces).thenReturn(arrayOf(fragmentSubcomponent))

        // when
        val daggerComponentsVisitor = DaggerComponentsVisitorImpl()
        daggerComponentsVisitor.visit(applicationComponent)

        // then
        arrayOf(applicationComponent, activitySubcomponentImpl, fragmentSubcomponentImpl)
            .forEach {
                val injectMethod = it.methods.find { method -> method.name == "inject" }
                val injectParam = injectMethod!!.parameterTypes.first().name
                verify(injectMethod).addLocalVariable("initialTime", CtClass.longType)
                verify(injectMethod).addLocalVariable("initialCpuTime", CtClass.longType)
                verify(injectMethod).addLocalVariable("endTime", CtClass.longType)
                verify(injectMethod).addLocalVariable("endCpuTime", CtClass.longType)
                verify(injectMethod).insertBefore(
                    """
                       long initialTime = com.droidsingh.daggertrack.DaggerTrackClocks.getUptimeMillis();
                       long initialCpuTime = com.droidsingh.daggertrack.DaggerTrackClocks.getCpuTimeMillis();
                    """.trimIndent()
                )
                verify(injectMethod).insertAfter(
                    """
                        long endTime = com.droidsingh.daggertrack.DaggerTrackClocks.getUptimeMillis();
                        long endCpuTime = com.droidsingh.daggertrack.DaggerTrackClocks.getCpuTimeMillis();
                        android.util.Log.d("DaggerTrack","Total time of ${injectParam}: " + (endTime - initialTime));
                        android.util.Log.d("DaggerTrack","Total On CPU time of ${injectParam}: " + (endCpuTime - initialCpuTime));
                        android.util.Log.d("DaggerTrack","Total Off CPU time of ${injectParam}: " + ((endTime - initialTime) - (endCpuTime - initialCpuTime)));
            """.trimIndent()
                )
            }
    }

    private fun prepareComponent(injectParam: String, componentName: String): CtClass {
        val injectMethod = mock<CtMethod>()
        val initialize = mock<CtMethod>()
        val component = mock<CtClass>()
        val injectParamClass = mock<CtClass>()
        whenever(component.methods).thenReturn(arrayOf(injectMethod, initialize))
        whenever(injectMethod.name).thenReturn("inject")
        whenever(initialize.name).thenReturn("initialize")
        whenever(injectParamClass.name).thenReturn(injectParam)
        whenever(injectMethod.parameterTypes).thenReturn(arrayOf(injectParamClass))
        whenever(component.declaredMethods).thenReturn(arrayOf(injectMethod, initialize))
        whenever(component.name).thenReturn(componentName)
        return component
    }
}