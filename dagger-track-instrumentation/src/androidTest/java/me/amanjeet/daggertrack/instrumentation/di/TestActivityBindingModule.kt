package me.amanjeet.daggertrack.instrumentation.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import me.amanjeet.daggertrack.instrumentation.DaggerTrackConsoleActivity
import me.amanjeet.daggertrack.instrumentation.di.qualifiers.PerActivity

@Module
abstract class TestActivityBindingModule {

    @PerActivity
    @ContributesAndroidInjector(modules = [DaggerTrackActivityModule::class])
    abstract fun daggerTrackConsoleActivity(): DaggerTrackConsoleActivity
}