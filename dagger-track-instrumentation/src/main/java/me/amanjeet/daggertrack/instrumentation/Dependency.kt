package me.amanjeet.daggertrack.instrumentation

data class AppDependency(val id: Int, val scope: Scope)

data class ActivityDependency(val id: Int, val scope: Scope)

enum class Scope {
    App,
    Activity
}