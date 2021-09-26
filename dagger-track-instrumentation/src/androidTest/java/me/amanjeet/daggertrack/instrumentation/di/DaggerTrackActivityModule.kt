package me.amanjeet.daggertrack.instrumentation.di

import dagger.Module
import dagger.Provides
import me.amanjeet.daggertrack.instrumentation.ActivityDependency
import me.amanjeet.daggertrack.instrumentation.Scope

@Module
class DaggerTrackActivityModule {

    @Provides
    fun providesActivityDependency(): ActivityDependency {
        return ActivityDependency(id = 2, scope = Scope.Activity)
    }
}