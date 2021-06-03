package me.amanjeet.daggertrack

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import javassist.ClassPool
import javassist.CtClass
import me.amanjeet.daggertrack.utils.addSubcomponentAnnotation
import org.junit.Test
import java.io.File

internal class CtClassTransformationsTest {

    private val classPool = ClassPool.getDefault()

    @Test
    fun `it filters out dagger components`() {
        // given
        classPool.makeClass("android.app.Application")
        classPool.makeClass("me.amanjeet.daggertrack.DaggerTrackApp")
        classPool.makeClass("dagger.BindsInstance")
        classPool.makeClass("dagger.Component")
        classPool.makeClass("javax.inject.Singleton")
        classPool.makeClass("dagger.Component.Builder")
        val applicationComponent = File(
            "./src/test/resources/me/amanjeet/daggertrack/di" +
                    "/components/ApplicationComponent.class"
        )
        val appComponent = mock<CtClass>()
        val memberInjectorClass = mock<CtClass>()
        val factoryClass = mock<CtClass>()
        val activityClass = mock<CtClass>()
        val appComponentInterface = arrayOf(classPool.makeClass(applicationComponent.inputStream()))
        val memberInjectorInterface = arrayOf(classPool.makeClass("dagger.MembersInjector"))
        val factoryInterface = arrayOf(classPool.makeClass("dagger.internal.Factory"))
        whenever(appComponent.interfaces).thenReturn(appComponentInterface)
        whenever(memberInjectorClass.interfaces).thenReturn(memberInjectorInterface)
        whenever(factoryClass.interfaces).thenReturn(factoryInterface)
        whenever(activityClass.interfaces).thenReturn(arrayOf())
        whenever(appComponent.name).thenReturn("DaggerApplicationComponent")
        val ctClassList = listOf(
            appComponent,
            memberInjectorClass,
            factoryClass,
            activityClass
        )

        // when
        val daggerComponents = ctClassList.filterDaggerComponents()

        // then
        val daggerComponentNames = daggerComponents.map { it.name }
        assertThat(daggerComponentNames).containsExactly("DaggerApplicationComponent")
    }

    @Test
    fun `it filters out all the subcomponents of component`() {
        // given
        val homeActivitySubcomponentImpl = classPool.makeClass(
            "me.amanjeet.daggertrack.di.components.HomeActivitySubcomponentImpl"
        )
        val homeFragmentSubcomponentImpl = classPool.makeClass(
            "me.amanjeet.daggertrack.di.components.HomeFragmentSubcomponentImpl"
        )
        // for convenience lets assume subcomponent class is static
        // dagger does not create static subcomponents
        // javassist does not supports to create non-static classes
        val homeFragmentNestedSubcomponentImpl = homeActivitySubcomponentImpl.makeNestedClass(
            homeFragmentSubcomponentImpl.name,
            true
        )
        prepareHomeActivity(homeActivitySubcomponentImpl)
        prepareHomeFragment(homeFragmentNestedSubcomponentImpl)
        val applicationComponent = mock<CtClass>()
        whenever(applicationComponent.nestedClasses).thenReturn(arrayOf(homeActivitySubcomponentImpl))

        // when
        val subComponentCtClassList = applicationComponent.filterSubcomponents()

        // then
        val subcomponentListNames = subComponentCtClassList.map { it.name }
        assertThat(subcomponentListNames).containsExactly(
            homeActivitySubcomponentImpl.name, homeFragmentNestedSubcomponentImpl.name
        )
    }

    private fun prepareHomeActivity(homeActivitySubcomponentImpl: CtClass) {
        val homeActivitySubcomponent = classPool.makeInterface(
            "me.amanjeet.daggertrack.di.components.HomeActivitySubcomponent"
        )
        homeActivitySubcomponentImpl.addInterface(homeActivitySubcomponent)
        homeActivitySubcomponent.addSubcomponentAnnotation()
    }

    private fun prepareHomeFragment(homeFragmentNestedSubcomponentImpl: CtClass) {
        val homeFragmentSubcomponent = classPool.makeInterface(
            "me.amanjeet.daggertrack.di.components.HomeFragmentSubcomponent"
        )
        homeFragmentNestedSubcomponentImpl.addInterface(homeFragmentSubcomponent)
        homeFragmentSubcomponent.addSubcomponentAnnotation()
    }
}