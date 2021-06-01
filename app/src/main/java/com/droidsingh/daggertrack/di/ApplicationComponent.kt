package com.droidsingh.daggertrack.di

import android.app.Application
import com.droidsingh.daggertrack.DaggerTrackApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        AppModule::class,
        ActivityBindingModule::class,
        AndroidSupportInjectionModule::class
    ]
)
interface ApplicationComponent {

    fun inject(daggerTrackApp: DaggerTrackApp)

    @Component.Builder
    interface Builder {
        fun bindApplication(@BindsInstance application: Application): Builder
        fun build(): ApplicationComponent
    }
}