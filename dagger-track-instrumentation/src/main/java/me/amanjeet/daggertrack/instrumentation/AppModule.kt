package me.amanjeet.daggertrack.instrumentation

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import me.amanjeet.daggertrack.instrumentation.di.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
abstract class AppModule {

    @Binds
    @ApplicationContext
    @Singleton
    abstract fun provideApplicationContext(application: Application): Context

    companion object {
        @Provides
        fun providesAppDependency() = AppDependency(1, Scope.App)
    }
}