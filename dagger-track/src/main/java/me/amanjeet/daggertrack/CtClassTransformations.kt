package me.amanjeet.daggertrack

import javassist.CtClass
import me.amanjeet.daggertrack.models.HiltComponent
import java.util.*

/**
 * Filters a list of [CtClass] to give dagger components
 */
internal fun List<CtClass>.filterDaggerComponents(): List<CtClass> {
    return filter {
        val daggerComponentInterface = it.interfaces.filter { interfaceClass ->
            interfaceClass.hasAnnotation("dagger.Component")
        }
        daggerComponentInterface.isNotEmpty()
    }
}

/**
 * Filters subcomponents of a component [CtClass]. Dagger generates subcomponents as nested class
 * in generated components or subcomponents. Currently, we only filter till 2nd level of nested
 * subcomponents.
 */
internal fun CtClass.filterSubcomponents(): List<CtClass> {
    return filterNestedSubcomponents()
        .flatMap { listOf(it) + it.filterNestedSubcomponents() }
}

private fun CtClass.filterNestedSubcomponents(): List<CtClass> {
    return nestedClasses.filter {
        val subcomponentInterface = it.interfaces.filter { interfaceClass ->
            interfaceClass.hasAnnotation("dagger.Subcomponent")
        }
        subcomponentInterface.isNotEmpty()
    }
}

/**
 * Filters dagger hilt components from the generated singleton hilt component. Traverses through
 * the nested classes for each hilt component and adds all the component in hierarchy to a list.
 *
 * @return list of [CtClass] representing dagger hilt components
 */
fun List<CtClass>.filterDaggerHiltComponents(): List<CtClass> {
    val componentList = mutableListOf<CtClass>()
    val generatedApplicationClass = first {
        it.superclass.name == "android.app.Application"
    }
    val queue = LinkedList<HiltComponent>()
    queue.add(HiltComponent.SingletonComponent)
    val singletonComponentClass = first {
        it.superclass.name == getGeneratedComponentName(
            generatedApplicationClass,
            HiltComponent.SingletonComponent
        )
    }
    componentList += singletonComponentClass
    while (!queue.isEmpty()) {
        val hiltComponent = queue.poll()
        val componentClass = componentList.first { it.name.contains(hiltComponent.componentName) }
        if (hiltComponent.getChildComponents().isNotEmpty()) {
            hiltComponent.getChildComponents().forEach { component ->
                val childComponents = componentClass.nestedClasses.filter {
                    it.superclass.name == getGeneratedComponentName(
                        generatedApplicationClass,
                        component
                    )
                }
                componentList += childComponents
                queue.add(component)
            }
        }
    }
    return componentList.toList()
}

private fun getGeneratedComponentName(
    generatedApplicationClass: CtClass?,
    hiltComponentName: HiltComponent
): String {
    val applicationClass = generatedApplicationClass?.name?.replace(
        "Hilt_", ""
    )
    return applicationClass + "_HiltComponents$" + hiltComponentName.componentName
}