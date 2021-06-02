package me.amanjeet.daggertrack.di

import dagger.Module
import dagger.Provides
import me.amanjeet.daggertrack.HeavyDependencyOne
import me.amanjeet.daggertrack.HeavyDependencyTwo

@Module
class HomeModule {

    @Provides
    fun providesHeavyDependencyOne(): HeavyDependencyOne {
        return me.amanjeet.daggertrack.HeavyDependencyOne()
    }

    @Provides
    fun providesHeavyDependencyTwo(): HeavyDependencyTwo {
        return me.amanjeet.daggertrack.HeavyDependencyTwo()
    }
}