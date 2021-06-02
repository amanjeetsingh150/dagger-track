package me.amanjeet.daggertrack

import javassist.CtClass

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