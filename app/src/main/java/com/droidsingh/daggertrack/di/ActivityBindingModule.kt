package com.droidsingh.daggertrack.di

import com.droidsingh.daggertrack.di.scopes.PerActivity
import com.droidsingh.daggertrack.ui.HomeActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {

    @PerActivity
    @ContributesAndroidInjector(modules = [HomeScreenModule::class])
    abstract fun homeActivity(): HomeActivity
}