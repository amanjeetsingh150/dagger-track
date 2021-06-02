package me.amanjeet.daggertrack.di

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module
import me.amanjeet.daggertrack.di.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
abstract class AppModule {

    @Binds
    @ApplicationContext
    @Singleton
    abstract fun provideApplicationContext(application: Application): Context
}