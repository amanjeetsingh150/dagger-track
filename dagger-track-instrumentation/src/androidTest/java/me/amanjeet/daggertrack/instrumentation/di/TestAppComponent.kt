package me.amanjeet.daggertrack.instrumentation.di

import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import me.amanjeet.daggertrack.instrumentation.AppModule
import me.amanjeet.daggertrack.instrumentation.DaggerTrackConsoleActivity
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        TestActivityBindingModule::class,
        AppModule::class,
        AndroidSupportInjectionModule::class,
        AndroidInjectionModule::class
    ]
)
interface TestAppComponent : AppComponent