package com.droidsingh.daggertrack.di

import android.app.Application
import android.content.Context
import com.droidsingh.daggertrack.di.qualifiers.ApplicationContext
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class AppModule {

    @Binds
    @ApplicationContext
    @Singleton
    abstract fun provideApplicationContext(application: Application): Context
}