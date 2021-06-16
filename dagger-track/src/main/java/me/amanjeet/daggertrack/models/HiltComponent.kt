package me.amanjeet.daggertrack.models

/**
 * A class representing dagger hilt component hierarchy.
 *
 * Hierarchy: https://dagger.dev/hilt/component-hierarchy.svg
 */
internal enum class HiltComponent(val componentName: String) {
    SingletonComponent("SingletonC") {
        override fun getChildComponents(): List<HiltComponent> {
            return listOf(ActivityRetainedComponent, ServiceComponent)
        }
    },
    ActivityRetainedComponent("ActivityRetainedC") {
        override fun getChildComponents(): List<HiltComponent> {
            return listOf(ActivityComponent, ViewModelComponent)
        }
    },
    ServiceComponent("ServiceC") {
        override fun getChildComponents(): List<HiltComponent> {
            return emptyList()
        }
    },
    ActivityComponent("ActivityC") {
        override fun getChildComponents(): List<HiltComponent> {
            return listOf(FragmentComponent, ViewComponent)
        }
    },
    ViewModelComponent("ViewModelC") {
        override fun getChildComponents(): List<HiltComponent> {
            return emptyList()
        }
    },
    FragmentComponent("FragmentC") {
        override fun getChildComponents(): List<HiltComponent> {
            return listOf(ViewWithFragmentComponent)
        }
    },
    ViewComponent("ViewC") {
        override fun getChildComponents(): List<HiltComponent> {
            return emptyList()
        }
    },
    ViewWithFragmentComponent("ViewWithFragmentC") {
        override fun getChildComponents(): List<HiltComponent> {
            return emptyList()
        }
    };

    abstract fun getChildComponents(): List<HiltComponent>
}