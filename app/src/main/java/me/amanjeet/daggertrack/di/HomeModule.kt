package me.amanjeet.daggertrack.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import me.amanjeet.daggertrack.HeavyDependencyOne
import me.amanjeet.daggertrack.HeavyDependencyTwo

@InstallIn(FragmentComponent::class)
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