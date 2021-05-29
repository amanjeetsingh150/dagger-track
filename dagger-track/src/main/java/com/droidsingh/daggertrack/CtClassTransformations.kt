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