package me.amanjeet.daggertrack.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import me.amanjeet.daggertrack.ui.HomeFragment

@Module
abstract class HomeScreenModule {

    @me.amanjeet.daggertrack.di.scopes.PerFragment
    @ContributesAndroidInjector(modules = [HomeModule::class])
    internal abstract fun homeFragment(): HomeFragment
}