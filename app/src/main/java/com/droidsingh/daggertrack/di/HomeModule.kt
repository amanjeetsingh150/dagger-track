package com.droidsingh.daggertrack.di

import com.droidsingh.daggertrack.HeavyDependencyOne
import com.droidsingh.daggertrack.HeavyDependencyTwo
import dagger.Module
import dagger.Provides

@Module
class HomeModule {

    @Provides
    fun providesHeavyDependencyOne(): HeavyDependencyOne {
        return HeavyDependencyOne()
    }

    @Provides
    fun providesHeavyDependencyTwo(): HeavyDependencyTwo {
        return HeavyDependencyTwo()
    }
}