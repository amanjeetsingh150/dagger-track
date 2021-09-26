package me.amanjeet.daggertrack.instrumentation.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import me.amanjeet.daggertrack.instrumentation.AppModule
import me.amanjeet.daggertrack.instrumentation.TestApplication
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        AppModule::class,
        AndroidSupportInjectionModule::class
    ]
)
interface AppComponent {
    fun inject(testApplication: TestApplication)

    @Component.Builder
    interface Builder {
        fun bindApplication(@BindsInstance application: Application): Builder
        fun build(): AppComponent
    }
}