package com.droidsingh.daggertrack.di

import com.droidsingh.daggertrack.di.scopes.PerFragment
import com.droidsingh.daggertrack.ui.HomeFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class HomeScreenModule {

    @PerFragment
    @ContributesAndroidInjector(modules = [HomeModule::class])
    internal abstract fun homeFragment(): HomeFragment
}