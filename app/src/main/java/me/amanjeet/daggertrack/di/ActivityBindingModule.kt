package me.amanjeet.daggertrack.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import me.amanjeet.daggertrack.di.scopes.PerActivity
import me.amanjeet.daggertrack.ui.HomeActivity

@Module
abstract class ActivityBindingModule {

    @PerActivity
    @ContributesAndroidInjector(modules = [HomeScreenModule::class])
    abstract fun homeActivity(): HomeActivity
}