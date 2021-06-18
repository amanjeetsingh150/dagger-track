package me.amanjeet.daggertrack.utils

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import javassist.CtClass

internal class DaggerComponentsFixtureCreator {

    companion object {
        const val FRAGMENT_C_IMPL = "me.amanjeet.daggertrack." +
                "DaggerDaggerTrackApp_HiltComponents_SingletonC.ActivityRetainedCImpl" +
                ".ActivityCImpl.FragmentCI"
        private const val FRAGMENT_C =
            "me.amanjeet.daggertrack.DaggerTrackApp_HiltComponents\$FragmentC"

        private const val VIEW_WITH_FRAGMENT_C =
            "me.amanjeet.daggertrack.DaggerTrackApp_HiltComponents\$ViewWithFragmentC"
        const val VIEW_WITH_FRAGMENT_C_IMPL = "me.amanjeet.daggertrack." +
                "DaggerDaggerTrackApp_HiltComponents_SingletonC.ActivityRetainedCImpl" +
                ".ActivityCImpl.FragmentCI.ViewWithFragmentCI"

        private const val VIEW_C = "me.amanjeet.daggertrack.DaggerTrackApp_HiltComponents\$ViewC"
        const val VIEW_C_IMPL =
            "me.amanjeet.daggertrack.DaggerDaggerTrackApp_HiltComponents_SingletonC." +
                    "ActivityRetainedCImpl.ActivityCImpl.ViewCI"

        private const val ACTIVITY_C =
            "me.amanjeet.daggertrack.DaggerTrackApp_HiltComponents\$ActivityC"
        const val ACTIVITY_C_IMPL = "me.amanjeet.daggertrack" +
                ".DaggerDaggerTrackApp_HiltComponents_SingletonC.ActivityRetainedCImpl.ActivityCImpl"

        private const val VIEWMODEL_C =
            "me.amanjeet.daggertrack.DaggerTrackApp_HiltComponents\$ViewModelC"
        const val VIEWMODEL_C_IMPL = "me.amanjeet.daggertrack." +
                "DaggerDaggerTrackApp_HiltComponents_SingletonC.ActivityRetainedCImpl.ViewModelCImpl"

        private const val ACTIVITY_RETAINED_C = "me.amanjeet.daggertrack." +
                "DaggerTrackApp_HiltComponents\$ActivityRetainedC"
        const val ACTIVITY_RETAINED_C_IMPL = "me.amanjeet.daggertrack." +
                "DaggerDaggerTrackApp_HiltComponents_SingletonC.ActivityRetainedCImpl"

        private const val SERVICE_C = "me.amanjeet.daggertrack.DaggerTrackApp_HiltComponents\$ServiceC"
        const val SERVICE_C_IMPL = "me.amanjeet.daggertrack." +
                "DaggerDaggerTrackApp_HiltComponents_SingletonC.ServiceCImpl"

        private const val SINGLETON_C = "me.amanjeet.daggertrack." +
                "DaggerTrackApp_HiltComponents\$SingletonC"
        const val SINGLETON_C_IMPL = "me.amanjeet.daggertrack" +
                ".DaggerDaggerTrackApp_HiltComponents_SingletonC"
    }

    fun createHiltComponentTree(): CtClass {
        val viewWithFragmentC = HiltComponentFixture(VIEW_WITH_FRAGMENT_C, VIEW_WITH_FRAGMENT_C_IMPL)
        val fragmentC = HiltComponentFixture(FRAGMENT_C, FRAGMENT_C_IMPL)
        val viewC = HiltComponentFixture(VIEW_C, VIEW_C_IMPL)
        val activityC = HiltComponentFixture(ACTIVITY_C, ACTIVITY_C_IMPL)
        val viewModelC = HiltComponentFixture(VIEWMODEL_C, VIEWMODEL_C_IMPL)
        val activityRetainedC = HiltComponentFixture(ACTIVITY_RETAINED_C, ACTIVITY_RETAINED_C_IMPL)
        val serviceC = HiltComponentFixture(SERVICE_C, SERVICE_C_IMPL)
        val singletonC = HiltComponentFixture(SINGLETON_C, SINGLETON_C_IMPL)

        val singletonCImpl = createComponent(singletonC)
        val activityRetainedCImpl = createComponent(activityRetainedC)
        val serviceCImpl = createComponent(serviceC)
        val viewModelCImpl = createComponent(viewModelC)
        val activityCImpl = createComponent(activityC)
        val viewCImpl = createComponent(viewC)
        val fragmentCImpl = createComponent(fragmentC)
        val viewWithFragmentCImpl = createComponent(viewWithFragmentC)

        singletonCImpl.addChildComponent(activityRetainedCImpl, serviceCImpl)
        activityRetainedCImpl.addChildComponent(activityCImpl, viewModelCImpl)
        activityCImpl.addChildComponent(viewCImpl, fragmentCImpl)
        fragmentCImpl.addChildComponent(viewWithFragmentCImpl)
        return singletonCImpl
    }

    data class HiltComponentFixture(val name: String, val implName: String)

    private fun CtClass.addChildComponent(vararg childComponents: CtClass) {
        whenever(nestedClasses).thenReturn(childComponents)
    }

    private fun createComponent(hiltFixtureComponents: HiltComponentFixture): CtClass {
        val componentImpl = mock<CtClass>()
        val component = mock<CtClass>()
        whenever(componentImpl.name).thenReturn(hiltFixtureComponents.implName)
        whenever(component.name).thenReturn(hiltFixtureComponents.name)
        whenever(componentImpl.superclass).thenReturn(component)
        return componentImpl
    }
}
