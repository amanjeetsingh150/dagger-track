package me.amanjeet.daggertrack.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import me.amanjeet.daggertrack.DaggerTrackApp
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