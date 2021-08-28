package me.amanjeet.daggertrack

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import javassist.Modifier
import me.amanjeet.daggertrack.utils.addSubcomponentAnnotation
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

internal class DaggerComponentsVisitorTest {

    @Rule
    @JvmField
    val testProjectDir: TemporaryFolder = TemporaryFolder
        .builder().assureDeletion().build()

    private val classPool = ClassPool.getDefault()
    private val applicationComponent = prepareComponent(
        injectParam = "me.amanjeet.daggertrack.DaggerTrackApp",
        componentName = "me.amanjeet.daggertrack.DaggerTrackApplicationComponent"
    )
    private val activitySubcomponentImpl = prepareComponent(
        injectParam = "me.amanjeet.daggertrack.ui.HomeActivity",
        componentName = "me.amanjeet.daggertrack.DaggerTrackApplicationComponent.HomeActivitySubcomponentImpl"
    )
    private val fragmentSubcomponentImpl = prepareComponent(
        injectParam = "me.amanjeet.daggertrack.ui.HomeFragment",
        componentName = "me.amanjeet.daggertrack.DaggerTrackApplicationComponent" +
                ".HomeActivitySubcomponentImpl.HomeFragmentSubcomponentImpl"
    )

    @Before
    fun setup() {
        val activitySubcomponent = classPool.makeInterface(
            "me.amanjeet.daggertrack.di.components.HomeActivitySubcomponent"
        )
        activitySubcomponent.addSubcomponentAnnotation()
        val fragmentSubcomponent = classPool.makeInterface(
            "me.amanjeet.daggertrack.di.components.HomeFragmentSubcomponent"
        )
        fragmentSubcomponent.addSubcomponentAnnotation()
        whenever(applicationComponent.nestedClasses).thenReturn(arrayOf(activitySubcomponentImpl))
        whenever(activitySubcomponentImpl.nestedClasses).thenReturn(arrayOf(fragmentSubcomponentImpl))
        whenever(activitySubcomponentImpl.interfaces).thenReturn(arrayOf(activitySubcomponent))
        whenever(fragmentSubcomponentImpl.interfaces).thenReturn(arrayOf(fragmentSubcomponent))
    }

    @Test
    fun `it visits hilt components and adds tracking logs in inject methods`() {
        // given
        val daggertrackApp = mock<CtClass>()
        val singletonComponent = mock<CtClass>()
        val injectMethod = mock<CtMethod>()
        val serviceComponentBuilder = mock<CtMethod>()
        val injectParam = "me.amanjeet.daggertrack.DaggerTrackApp"
        whenever(serviceComponentBuilder.name).thenReturn("serviceComponentBuilder")
        whenever(daggertrackApp.name).thenReturn(injectParam)
        whenever(injectMethod.parameterTypes).thenReturn(arrayOf(daggertrackApp))
        whenever(injectMethod.name).thenReturn("injectDaggerTrackApp")
        whenever(injectMethod.modifiers).thenReturn(Modifier.PUBLIC)
        whenever(singletonComponent.declaredMethods).thenReturn(arrayOf(injectMethod))

        // when
        val daggerComponentsVisitor = DaggerComponentsVisitorImpl()
        daggerComponentsVisitor.visitDaggerHiltComponents(singletonComponent)

        // then
        verify(injectMethod).addLocalVariable("initialTime", CtClass.longType)
        verify(injectMethod).addLocalVariable("initialCpuTime", CtClass.longType)
        verify(injectMethod).insertBefore(
            """
              long initialTime = me.amanjeet.daggertrack.DaggerTrackClocks.getUptimeMillis();
              long initialCpuTime = me.amanjeet.daggertrack.DaggerTrackClocks.getCpuTimeMillis();
            """.trimIndent()
        )
        verify(injectMethod).addLocalVariable("endTime", CtClass.longType)
        verify(injectMethod).addLocalVariable("endCpuTime", CtClass.longType)
        verify(injectMethod).insertAfter(
            """
                long endTime = me.amanjeet.daggertrack.DaggerTrackClocks.getUptimeMillis();
                long endCpuTime = me.amanjeet.daggertrack.DaggerTrackClocks.getCpuTimeMillis();
                android.util.Log.d("DaggerTrack","Total time of ${injectParam}: " + (endTime - initialTime) + "ms");
                android.util.Log.d("DaggerTrack","Total On CPU time of ${injectParam}: " + (endCpuTime - initialCpuTime) + "ms");
                android.util.Log.d("DaggerTrack","Total Off CPU time of ${injectParam}: " + ((endTime - initialTime) - (endCpuTime - initialCpuTime)) + "ms");
            """.trimIndent()
        )
        verify(serviceComponentBuilder, never()).addLocalVariable("initialTime", CtClass.longType)
        verify(serviceComponentBuilder, never()).addLocalVariable("initialCpuTime", CtClass.longType)
        verify(serviceComponentBuilder, never()).insertBefore(
            """
              long initialTime = me.amanjeet.daggertrack.DaggerTrackClocks.getUptimeMillis();
              long initialCpuTime = me.amanjeet.daggertrack.DaggerTrackClocks.getCpuTimeMillis();
            """.trimIndent()
        )
    }

    @Test
    fun `it defrost the component if its frozen`() {
        // given
        whenever(applicationComponent.isFrozen).thenReturn(true)
        whenever(activitySubcomponentImpl.isFrozen).thenReturn(true)
        whenever(fragmentSubcomponentImpl.isFrozen).thenReturn(true)

        // when
        val daggerComponentsVisitor = DaggerComponentsVisitorImpl()
        daggerComponentsVisitor.visitDaggerAndroidComponents(applicationComponent)

        // then
        verify(applicationComponent).defrost()
        verify(activitySubcomponentImpl).defrost()
        verify(fragmentSubcomponentImpl).defrost()
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